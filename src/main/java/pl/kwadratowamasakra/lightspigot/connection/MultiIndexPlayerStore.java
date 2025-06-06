package pl.kwadratowamasakra.lightspigot.connection;

import pl.kwadratowamasakra.lightspigot.connection.user.PlayerConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class MultiIndexPlayerStore {
    private final List<PlayerConnection> players = new ArrayList<>();
    private final Map<String, PlayerConnection> byName = new HashMap<>();
    private final Map<String, PlayerConnection> byNameLowerCase = new HashMap<>();
    private final Map<String, List<PlayerConnection>> byIp = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public void add(PlayerConnection item) {
        writeLock.lock();
        try {
            players.add(item);
            byName.put(item.getName(), item);
            byNameLowerCase.put(item.getNameLowerCase(), item);
            byIp.computeIfAbsent(item.getIp(), k -> new ArrayList<>()).add(item);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean remove(PlayerConnection item) {
        writeLock.lock();
        try {
            if (players.remove(item)) {
                byName.remove(item.getName());
                byNameLowerCase.remove(item.getNameLowerCase());

                List<PlayerConnection> players = byIp.get(item.getIp());
                if (players != null) {
                    players.remove(item);
                    if (players.isEmpty()) {
                        byIp.remove(item.getIp());
                    }
                }

                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    public PlayerConnection getByName(String name) {
        readLock.lock();
        try {
            return byName.get(name);
        } finally {
            readLock.unlock();
        }
    }

    public PlayerConnection getByNameLowerCase(String nameLower) {
        readLock.lock();
        try {
            return byNameLowerCase.get(nameLower.toLowerCase());
        } finally {
            readLock.unlock();
        }
    }

    public List<PlayerConnection> getByIp(String ip) {
        readLock.lock();
        try {
            return new ArrayList<>(byIp.get(ip));
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

    public void doActionOnAll(Consumer<PlayerConnection> action) {
        readLock.lock();
        try {
            players.forEach(action);
        } finally {
            readLock.unlock();
        }
    }
}
