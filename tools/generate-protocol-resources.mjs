import fs from 'node:fs'
import os from 'node:os'
import path from 'node:path'
import { spawnSync } from 'node:child_process'

// Source data: https://github.com/PrismarineJS/minecraft-data (MIT)

const argumentsList = process.argv.slice(2)
const dataRoot = argumentsList[0]
const data26Root = argumentsList.length === 2 ? dataRoot : argumentsList[1]
const serverJarsRoot = argumentsList.length === 2 ? argumentsList[1] : argumentsList[2]
const capturedPayloadsRoot = argumentsList[3]
if (argumentsList.length < 2 || argumentsList.length > 4 || !dataRoot || !serverJarsRoot) {
  throw new Error('Usage: node tools/generate-protocol-resources.mjs <minecraft-data/data> [minecraft-data-26.2/data] <vanilla-server-jars> [captured-protocol-payloads]')
}

const targetOutputRoot = path.resolve('src/main/resources/protocol')
const outputRoot = fs.mkdtempSync(path.join(os.tmpdir(), 'kmlight-protocol-resources-'))
process.on('exit', () => fs.rmSync(outputRoot, { recursive: true, force: true }))
const readJson = file => JSON.parse(fs.readFileSync(file, 'utf8'))
const dataPaths = readJson(path.join(dataRoot, 'dataPaths.json')).pc
const legacy = readJson(path.join(dataRoot, 'pc/common/legacy.json'))
const versions = [
  [393, '1.13'], [401, '1.13.1'], [404, '1.13.2'], [477, '1.14'], [480, '1.14.1'],
  [485, '1.14.2', '1.14.1'], [490, '1.14.3'], [498, '1.14.4'], [573, '1.15'],
  [575, '1.15.1'], [578, '1.15.2'], [735, '1.16'], [736, '1.16.1'], [751, '1.16.2'],
  [753, '1.16.3', '1.16.2'], [754, '1.16.4', '1.16.2'], [755, '1.17'], [756, '1.17.1'],
  [757, '1.18'], [758, '1.18.2'], [759, '1.19'], [760, '1.19.2'], [761, '1.19.3'],
  [762, '1.19.4'], [763, '1.20'], [764, '1.20.2'], [765, '1.20.3', '1.20.2'],
  [766, '1.20.5'], [767, '1.21.1'], [768, '1.21.3'], [769, '1.21.4'],
  [770, '1.21.5'], [771, '1.21.6'], [772, '1.21.8'], [773, '1.21.9'],
  [774, '1.21.11'], [775, '26.1'], [776, '26.2']
]

const aliases = new Map([
  ['grass_path', 'dirt_path'], ['rose_red', 'red_dye'], ['cactus_green', 'green_dye'],
  ['dandelion_yellow', 'yellow_dye'], ['sign', 'oak_sign'], ['wall_sign', 'oak_wall_sign'],
  ['wooden_door', 'oak_door'],
  ['boat', 'oak_boat'], ['reeds', 'sugar_cane'], ['slime', 'slime_block']
])

function rootFor(version) {
  return version === '26.2' && data26Root ? data26Root : dataRoot
}

function resolveDataFile(version, kind) {
  const root = rootFor(version)
  const paths = root === dataRoot ? dataPaths : readJson(path.join(root, 'dataPaths.json')).pc
  const ref = paths[version]?.[kind]
  if (!ref) throw new Error(`No ${kind} data for ${version}`)
  return path.join(root, `${ref}/${kind}.json`)
}

function parseState(value) {
  const match = value.match(/^minecraft:([^\[]+)(?:\[([^\]]+)\])?$/)
  if (!match) return { name: 'air', properties: new Map() }
  const properties = new Map()
  if (match[2]) for (const entry of match[2].split(',')) {
    const separator = entry.indexOf('=')
    properties.set(entry.slice(0, separator), entry.slice(separator + 1))
  }
  return { name: match[1], properties }
}

function stateValueIndex(state, value) {
  if (state.type === 'bool') return value === 'true' ? 0 : 1
  if (state.values) return state.values.indexOf(value)
  const numeric = Number(value)
  return Number.isFinite(numeric) ? numeric - (state.min ?? 0) : -1
}

function decodeDefault(block) {
  let offset = block.defaultState - block.minStateId
  const result = new Map()
  for (let index = block.states.length - 1; index >= 0; index--) {
    const state = block.states[index]
    const valueIndex = offset % state.num_values
    offset = Math.floor(offset / state.num_values)
    const value = state.type === 'bool' ? (valueIndex === 0 ? 'true' : 'false')
      : state.values ? state.values[valueIndex] : String((state.min ?? 0) + valueIndex)
    result.set(state.name, value)
  }
  return result
}

function stateId(block, requested) {
  const properties = decodeDefault(block)
  for (const [key, value] of requested) if (block.states.some(state => state.name === key)) properties.set(key, value)
  let offset = 0
  for (const state of block.states) {
    let valueIndex = stateValueIndex(state, properties.get(state.name))
    if (valueIndex < 0 || valueIndex >= state.num_values) valueIndex = 0
    offset = offset * state.num_values + valueIndex
  }
  return block.minStateId + offset
}

function findNamed(byName, originalName) {
  let name = originalName
  for (let attempts = 0; attempts < 3; attempts++) {
    const value = byName.get(name)
    if (value) return value
    name = aliases.get(name)
    if (!name) return undefined
  }
}

const chunks = []
const header = Buffer.alloc(6)
header.writeInt32BE(0x4B4D5031, 0)
header.writeUInt16BE(versions.length, 4)
chunks.push(header)

for (const [protocol, version, dataVersion = version] of versions) {
  const blocks = readJson(resolveDataFile(dataVersion, 'blocks'))
  const items = readJson(resolveDataFile(dataVersion, 'items'))
  const blocksByName = new Map(blocks.map(block => [block.name, block]))
  const itemsByName = new Map(items.map(item => [item.name, item]))
  const blockStates = new Int32Array(4096)
  for (const [legacyKey, modernValue] of Object.entries(legacy.blocks)) {
    const [id, data] = legacyKey.split(':').map(Number)
    const parsed = parseState(modernValue)
    if (parsed.name.endsWith('_stairs')) parsed.properties.set('shape', 'straight')
    const block = findNamed(blocksByName, parsed.name)
    if (block && id < 256 && data < 16) blockStates[id * 16 + data] = stateId(block, parsed.properties)
  }
  const itemMappings = []
  for (const [legacyKey, modernValue] of Object.entries(legacy.items)) {
    const [id, data] = legacyKey.split(':').map(Number)
    const parsed = parseState(modernValue)
    const item = findNamed(itemsByName, parsed.name)
    if (item) itemMappings.push([id * 16 + data, item.id])
  }
  const versionHeader = Buffer.alloc(4 + 4096 * 4 + 2 + itemMappings.length * 8)
  let offset = 0
  versionHeader.writeInt32BE(protocol, offset); offset += 4
  for (const state of blockStates) { versionHeader.writeInt32BE(state, offset); offset += 4 }
  versionHeader.writeUInt16BE(itemMappings.length, offset); offset += 2
  for (const [legacyValue, itemId] of itemMappings) {
    versionHeader.writeInt32BE(legacyValue, offset); offset += 4
    versionHeader.writeInt32BE(itemId, offset); offset += 4
  }
  chunks.push(versionHeader)
}
fs.writeFileSync(path.join(outputRoot, 'legacy-mappings.bin'), Buffer.concat(chunks))

const tagIds = { byte: 1, short: 2, int: 3, long: 4, float: 5, double: 6, byteArray: 7, string: 8, list: 9, compound: 10, intArray: 11, longArray: 12 }
const utf = value => {
  const text = Buffer.from(value, 'utf8')
  const header = Buffer.alloc(2); header.writeUInt16BE(text.length)
  return Buffer.concat([header, text])
}
function payload(tag) {
  const value = tag.value
  switch (tag.type) {
    case 'byte': { const b = Buffer.alloc(1); b.writeInt8(value); return b }
    case 'short': { const b = Buffer.alloc(2); b.writeInt16BE(value); return b }
    case 'int': { const b = Buffer.alloc(4); b.writeInt32BE(value); return b }
    case 'long': { const b = Buffer.alloc(8); b.writeInt32BE(value[0], 0); b.writeUInt32BE(value[1] >>> 0, 4); return b }
    case 'float': { const b = Buffer.alloc(4); b.writeFloatBE(value); return b }
    case 'double': { const b = Buffer.alloc(8); b.writeDoubleBE(value); return b }
    case 'string': return utf(value)
    case 'byteArray': { const b = Buffer.alloc(4); b.writeInt32BE(value.length); return Buffer.concat([b, Buffer.from(value)]) }
    case 'intArray': { const b = Buffer.alloc(4 + value.length * 4); b.writeInt32BE(value.length); value.forEach((v, i) => b.writeInt32BE(v, 4 + i * 4)); return b }
    case 'longArray': { const b = Buffer.alloc(4 + value.length * 8); b.writeInt32BE(value.length); value.forEach((v, i) => { b.writeInt32BE(v[0], 4 + i * 8); b.writeUInt32BE(v[1] >>> 0, 8 + i * 8) }); return b }
    case 'list': {
      const type = tagIds[value.type]
      const length = Buffer.alloc(5); length.writeUInt8(type, 0); length.writeInt32BE(value.value.length, 1)
      return Buffer.concat([length, ...value.value.map(entry => payload({ type: value.type, value: entry }))])
    }
    case 'compound': {
      const entries = []
      for (const [name, child] of Object.entries(value)) entries.push(Buffer.from([tagIds[child.type]]), utf(name), payload(child))
      entries.push(Buffer.from([0])); return Buffer.concat(entries)
    }
    default: throw new Error(`Unsupported NBT type ${tag.type}`)
  }
}
function nbt(tag) { return Buffer.concat([Buffer.from([tagIds[tag.type]]), utf(tag.name ?? ''), payload(tag)]) }
function anonymousNbt(tag) { return Buffer.concat([Buffer.from([tagIds[tag.type]]), payload(tag)]) }
function varInt(value) {
  const bytes = []
  do {
    let current = value & 0x7f
    value >>>= 7
    if (value) current |= 0x80
    bytes.push(current)
  } while (value)
  return Buffer.from(bytes)
}
function string(value) {
  const encoded = Buffer.from(value, 'utf8')
  return Buffer.concat([varInt(encoded.length), encoded])
}

function walkJsonFiles(root, relative = '') {
  const directory = path.join(root, relative)
  if (!fs.existsSync(directory)) return []
  const result = []
  for (const entry of fs.readdirSync(directory, { withFileTypes: true }).sort((a, b) => a.name.localeCompare(b.name))) {
    const child = path.join(relative, entry.name)
    if (entry.isDirectory()) result.push(...walkJsonFiles(root, child))
    else if (entry.isFile() && entry.name.endsWith('.json')) result.push(child)
  }
  return result
}

function findServerJar(version, protocol) {
  const candidates = [
    `${version}.jar`, `server-${version}.jar`, `${protocol}.jar`, `server-${protocol}.jar`
  ]
  for (const candidate of candidates) {
    const file = path.resolve(serverJarsRoot, candidate)
    if (fs.existsSync(file)) return file
  }
  throw new Error(`Missing vanilla server JAR for ${version} (protocol ${protocol}) in ${serverJarsRoot}`)
}

function generateVanillaData(version, protocol) {
  const prepared = path.resolve(serverJarsRoot, version)
  if (fs.existsSync(path.join(prepared, 'data/minecraft/tags'))
      && fs.existsSync(path.join(prepared, 'reports/registries.json'))) {
    return { root: prepared, cleanup: () => {} }
  }

  const temporary = fs.mkdtempSync(path.join(os.tmpdir(), `kmlight-protocol-${protocol}-`))
  const output = path.join(temporary, 'generated')
  const result = spawnSync('java', [
    '-DbundlerMainClass=net.minecraft.data.Main', '-jar', findServerJar(version, protocol),
    '--server', '--reports', '--output', output
  ], { cwd: temporary, stdio: 'inherit' })
  if (result.error) {
    fs.rmSync(temporary, { recursive: true, force: true })
    throw result.error
  }
  if (result.status !== 0) {
    fs.rmSync(temporary, { recursive: true, force: true })
    throw new Error(`Vanilla data generator failed for ${version} with exit code ${result.status}`)
  }
  return {
    root: output,
    cleanup: () => fs.rmSync(temporary, { recursive: true, force: true })
  }
}

function registryIds(vanillaRoot, login) {
  const report = readJson(path.join(vanillaRoot, 'reports/registries.json'))
  const result = new Map()
  for (const [registryName, registry] of Object.entries(report)) {
    if (!registry.entries) continue
    result.set(registryName, new Map(Object.entries(registry.entries)
      .map(([name, entry]) => [name, entry.protocol_id])))
  }
  for (const registry of Object.values(login.dimensionCodec)) {
    result.set(registry.id, new Map(registry.entries.map((entry, index) => [entry.key, index])))
  }
  return result
}

function encodeVanillaTags(vanillaRoot, login) {
  const idsByRegistry = registryIds(vanillaRoot, login)
  const tagsRoot = path.join(vanillaRoot, 'data/minecraft/tags')
  const registries = new Map()
  for (const relative of walkJsonFiles(tagsRoot)) {
    const parts = relative.split(path.sep)
    let registryName
    let tagParts
    for (let length = parts.length - 1; length >= 1; length--) {
      const candidate = `minecraft:${parts.slice(0, length).join('/')}`
      if (idsByRegistry.has(candidate)) {
        registryName = candidate
        tagParts = parts.slice(length)
        break
      }
    }
    if (!registryName) continue // server-only datapack registry, not synchronized to clients
    const tagName = `minecraft:${tagParts.join('/').replace(/\.json$/, '')}`
    const registry = registries.get(registryName) ?? new Map()
    registry.set(tagName, readJson(path.join(tagsRoot, relative)).values ?? [])
    registries.set(registryName, registry)
  }

  const bodies = []
  const sortedRegistries = [...registries.entries()].sort(([left], [right]) => left.localeCompare(right))
  bodies.push(varInt(sortedRegistries.length))
  for (const [registryName, definitions] of sortedRegistries) {
    const ids = idsByRegistry.get(registryName)
    const resolved = new Map()
    const resolving = new Set()
    const resolveTag = tagName => {
      if (resolved.has(tagName)) return resolved.get(tagName)
      if (resolving.has(tagName)) throw new Error(`Circular tag reference ${registryName} / ${tagName}`)
      const values = definitions.get(tagName)
      if (!values) throw new Error(`Missing required tag ${registryName} / ${tagName}`)
      resolving.add(tagName)
      const entries = []
      const seen = new Set()
      for (const rawValue of values) {
        const descriptor = typeof rawValue === 'string' ? { id: rawValue, required: true } : rawValue
        const value = descriptor.id.startsWith('minecraft:') || descriptor.id.startsWith('#minecraft:')
          ? descriptor.id : descriptor.id.startsWith('#') ? `#minecraft:${descriptor.id.slice(1)}` : `minecraft:${descriptor.id}`
        let additions
        if (value.startsWith('#')) {
          const referenced = value.slice(1)
          if (!definitions.has(referenced) && descriptor.required === false) continue
          additions = resolveTag(referenced)
        } else {
          const id = ids.get(value)
          if (id === undefined && descriptor.required === false) continue
          if (id === undefined) throw new Error(`Unknown required tag value ${registryName} / ${tagName}: ${value}`)
          additions = [id]
        }
        for (const id of additions) if (!seen.has(id)) { seen.add(id); entries.push(id) }
      }
      resolving.delete(tagName)
      resolved.set(tagName, entries)
      return entries
    }

    const sortedTags = [...definitions.keys()].sort()
    bodies.push(string(registryName), varInt(sortedTags.length))
    for (const tagName of sortedTags) {
      const entries = resolveTag(tagName)
      bodies.push(string(tagName), varInt(entries.length), ...entries.map(varInt))
    }
  }
  return Buffer.concat(bodies)
}

for (const version of ['1.16', '1.16.2', '1.17']) {
  const login = readJson(path.join(dataRoot, `pc/${version}/loginPacket.json`))
  fs.writeFileSync(path.join(outputRoot, `dimension-codec-${version}.nbt`), nbt(login.dimensionCodec))
  if (version !== '1.16') {
    const dimensions = login.dimensionCodec.value['minecraft:dimension_type'].value.value.value.value
    const names = new Map([
      ['minecraft:overworld', 'overworld'], ['minecraft:the_nether', 'nether'], ['minecraft:the_end', 'end']
    ])
    for (const entry of dimensions) {
      const fileName = names.get(entry.name.value)
      if (fileName) fs.writeFileSync(path.join(outputRoot, `dimension-${version}-${fileName}.nbt`),
        nbt({ ...entry.element, name: '' }))
    }
  }
}

for (const [protocol, version, dataVersion = version] of versions.filter(([protocol]) => protocol >= 757 && protocol <= 763)) {
  const login = readJson(resolveDataFile(dataVersion, 'loginPacket'))
  fs.writeFileSync(path.join(outputRoot, `dimension-codec-${protocol}.nbt`), nbt(login.dimensionCodec))
  if (protocol <= 758) {
    const registry = login.dimensionCodec.value['minecraft:dimension_type'].value.value.value.value
    const overworld = registry.find(entry => entry.name.value === 'minecraft:overworld')
    fs.writeFileSync(path.join(outputRoot, `dimension-${protocol}-overworld.nbt`), nbt({ ...overworld.element, name: '' }))
  }
}

function copyCapturedPayload(file) {
  const source = path.resolve(capturedPayloadsRoot, file)
  if (!fs.existsSync(source) || fs.statSync(source).size === 0) {
    throw new Error(`Missing captured vanilla payload ${file} in ${capturedPayloadsRoot}`)
  }
  fs.copyFileSync(source, path.join(outputRoot, file))
}

// Configuration registry packets. The file contains packet bodies rather than packet IDs,
// since those are registered separately and changed independently from the registry format.
for (const [protocol, version, dataVersion = version] of versions.filter(([protocol]) => protocol >= 764)) {
  if (capturedPayloadsRoot) {
    copyCapturedPayload(`configuration-${protocol}.bin`)
    continue
  }
  const login = readJson(resolveDataFile(dataVersion, 'loginPacket'))
  const bodies = []
  if (protocol <= 765) {
    bodies.push(anonymousNbt(login.dimensionCodec))
  } else {
    for (const registry of Object.values(login.dimensionCodec)) {
      const entries = [string(registry.id), varInt(registry.entries.length)]
      for (const entry of registry.entries) {
        entries.push(string(entry.key), Buffer.from([entry.value ? 1 : 0]))
        if (entry.value) entries.push(anonymousNbt(entry.value))
      }
      bodies.push(Buffer.concat(entries))
    }
  }
  const encoded = [Buffer.from([bodies.length >>> 8, bodies.length & 0xff])]
  for (const body of bodies) {
    const length = Buffer.alloc(4)
    length.writeUInt32BE(body.length)
    encoded.push(length, body)
  }
  fs.writeFileSync(path.join(outputRoot, `configuration-${protocol}.bin`), Buffer.concat(encoded))
}

// Since 1.20.5 clients require the real contents of vanilla tags. minecraft-data
// does not contain all static registries or datapack tags, so these are generated
// from the matching official server JAR and its registry report.
for (const [protocol, version, dataVersion = version] of versions.filter(([protocol]) => protocol >= 766)) {
  if (capturedPayloadsRoot) {
    copyCapturedPayload(`tags-${protocol}.bin`)
    continue
  }
  const vanilla = generateVanillaData(version, protocol)
  try {
    const login = readJson(resolveDataFile(dataVersion, 'loginPacket'))
    fs.writeFileSync(path.join(outputRoot, `tags-${protocol}.bin`), encodeVanillaTags(vanilla.root, login))
  } finally {
    vanilla.cleanup()
  }
}

const requiredFiles = [
  'legacy-mappings.bin',
  ...['1.16', '1.16.2', '1.17'].map(version => `dimension-codec-${version}.nbt`),
  ...['1.16.2', '1.17'].flatMap(version => ['overworld', 'nether', 'end']
    .map(dimension => `dimension-${version}-${dimension}.nbt`)),
  ...versions.filter(([protocol]) => protocol >= 757 && protocol <= 763)
    .map(([protocol]) => `dimension-codec-${protocol}.nbt`),
  ...versions.filter(([protocol]) => protocol >= 757 && protocol <= 758)
    .map(([protocol]) => `dimension-${protocol}-overworld.nbt`),
  ...versions.filter(([protocol]) => protocol >= 764).map(([protocol]) => `configuration-${protocol}.bin`),
  ...versions.filter(([protocol]) => protocol >= 766).map(([protocol]) => `tags-${protocol}.bin`)
]
for (const file of requiredFiles) {
  const generated = path.join(outputRoot, file)
  if (!fs.existsSync(generated) || fs.statSync(generated).size === 0) {
    throw new Error(`Generator did not create required resource ${file}`)
  }
}
fs.mkdirSync(targetOutputRoot, { recursive: true })
for (const file of fs.readdirSync(outputRoot)) {
  fs.copyFileSync(path.join(outputRoot, file), path.join(targetOutputRoot, file))
}
for (const file of fs.readdirSync(targetOutputRoot)) {
  if (file === 'legacy-mappings.bin'
      || file.startsWith('dimension-')
      || file.startsWith('configuration-')
      || file.startsWith('tags-')) {
    if (fs.existsSync(path.join(outputRoot, file))) continue
    fs.unlinkSync(path.join(targetOutputRoot, file))
  }
}
fs.rmSync(outputRoot, { recursive: true, force: true })
console.log(`Generated ${requiredFiles.length} protocol resources in ${targetOutputRoot}`)
