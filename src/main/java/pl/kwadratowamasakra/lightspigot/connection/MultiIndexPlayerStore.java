package pl.kwadratowamasakra.lightspigot.connection;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MultiIndexPlayerStore {
    private final List<PlayerConnection> players = new ArrayList<>();
    private final Set<String> playersConnecting = new HashSet<>();
    private final Map<String, PlayerConnection> byName = new HashMap<>();
    private final Map<String, PlayerConnection> byNameLowerCase = new HashMap<>();
    private final Map<String, List<PlayerConnection>> byIp = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public ConnectionManager.TryAddResult tryAddConnecting(final String name, final int maxPlayers) {
        final String nameLowerCase = name.toLowerCase();
        writeLock.lock();
        try {
            if (players.size() + playersConnecting.size() >= maxPlayers) {
                return ConnectionManager.TryAddResult.MAX_PLAYERS_REACHED;
            }
            if (byNameLowerCase.get(nameLowerCase) != null || playersConnecting.contains(nameLowerCase)) {
                return ConnectionManager.TryAddResult.ALREADY_CONNECTED;
            }
            playersConnecting.add(nameLowerCase);
            return ConnectionManager.TryAddResult.SUCCESS;
        } finally {
            writeLock.unlock();
        }
    }

    public void add(final PlayerConnection connection) {
        writeLock.lock();
        try {
            players.add(connection);
            playersConnecting.remove(connection.getNameLowerCase());
            byName.put(connection.getName(), connection);
            byNameLowerCase.put(connection.getNameLowerCase(), connection);
            byIp.computeIfAbsent(connection.getIp(), k -> new ArrayList<>()).add(connection);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean remove(final PlayerConnection connection) {
        writeLock.lock();
        try {
            if (connection.getName() != null) {
                playersConnecting.remove(connection.getNameLowerCase());
            }
            if (players.remove(connection)) {
                byName.remove(connection.getName());
                byNameLowerCase.remove(connection.getNameLowerCase());

                List<PlayerConnection> players = byIp.get(connection.getIp());
                if (players != null) {
                    players.remove(connection);
                    if (players.isEmpty()) {
                        byIp.remove(connection.getIp());
                    }
                }

                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public PlayerConnection getByName(final String name) {
        readLock.lock();
        try {
            return byName.get(name);
        } finally {
            readLock.unlock();
        }
    }

    public PlayerConnection getByNameLowerCase(final String name) {
        readLock.lock();
        try {
            return byNameLowerCase.get(name.toLowerCase());
        } finally {
            readLock.unlock();
        }
    }

    public List<PlayerConnection> getByIp(final String ip) {
        readLock.lock();
        try {
            return new ArrayList<>(byIp.getOrDefault(ip, Collections.emptyList()));
        } finally {
            readLock.unlock();
        }
    }

    public List<PlayerConnection> getAll() {
        readLock.lock();
        try {
            return new ArrayList<>(players);
        } finally {
            readLock.unlock();
        }
    }

    public void doActionOnAll(final Consumer<PlayerConnection> action) {
        readLock.lock();
        try {
            players.forEach(action);
        } finally {
            readLock.unlock();
        }
    }
}
