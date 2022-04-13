package cn.cruder.tools.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 带有效期map 简单实现 实现了基本的方法
 *
 * @author dousx
 */
public class ExpiryMap<K, V> extends ConcurrentHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    /**
     * 用于定时清除已过期缓存
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    /**
     * 同步锁
     */
    private Lock lock;

    /**
     * value为过期时间
     */
    private ConcurrentHashMap<K, Long> expiryMap = new ConcurrentHashMap<>();

    /**
     * default expiry time 2m
     */
    private long expiry = 1000 * 60 * 2;


    /**
     * @param expiryTime 单位毫秒（不可小于0）
     */
    public ExpiryMap(long expiryTime) {
        this(1 << 4, expiryTime);
    }

    /**
     * @param initialCapacity 初始容量
     * @param expiryTime      单位毫秒
     */
    public ExpiryMap(int initialCapacity, long expiryTime) {
        super(initialCapacity);
        if (expiryTime <= 0) {
            throw new RuntimeException();
        }
        this.expiry = expiryTime;
        this.lock = new ReentrantLock();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                clearExpiredCache();
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public V put(K key, V value) {
        lock.lock();
        try {
            expiryMap.put(key, System.currentTimeMillis() + this.expiry);
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        lock.lock();
        try {
            return !checkExpiry(key, true) && super.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param key        key
     * @param value      value
     * @param expiryTime 键值对有效期 毫秒
     * @return value
     */
    public V put(K key, V value, long expiryTime) {
        lock.lock();
        try {
            expiryMap.put(key, System.currentTimeMillis() + expiryTime);
            return super.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return size() == 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return Boolean.FALSE;
        }
        lock.lock();
        try {
            Set<Entry<K, V>> set = super.entrySet();
            if (set == null) {
                return Boolean.FALSE;
            }
            Iterator<Entry<K, V>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Entry<K, V> entry = iterator.next();
                if (value.equals(entry.getValue())) {
                    if (checkExpiry(entry.getKey(), false)) {
                        iterator.remove();
                        return Boolean.FALSE;
                    }
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        lock.lock();
        try {
            clearExpiredCache();
            return super.values();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            return null;
        }
        lock.lock();
        try {
            if (checkExpiry(key, true)) {
                return null;
            }
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }


    public Object isInvalid(Object key) {
        if (key == null) {
            return null;
        }
        lock.lock();
        try {
            if (!expiryMap.containsKey(key)) {
                return null;
            }
            long expiryTime = expiryMap.get(key);
            boolean flag = System.currentTimeMillis() > expiryTime;
            if (flag) {
                super.remove(key);
                expiryMap.remove(key);
                return -1;
            }
            return super.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        lock.lock();
        try {
            long expiryTime = System.currentTimeMillis() + this.expiry;
            for (Entry<? extends K, ? extends V> e : m.entrySet()) {
                expiryMap.put(e.getKey(), expiryTime);
            }
            super.putAll(m);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        lock.lock();
        try {
            Set<Entry<K, V>> set = super.entrySet();
            if (set != null) {
                set.removeIf(entry -> checkExpiry(entry.getKey(), false));
            }
            return set;
        } finally {
            lock.unlock();
        }
    }


    private boolean checkExpiry(Object key, boolean isRemoveSuper) {
        lock.lock();
        try {
            if (!expiryMap.containsKey(key)) {
                return Boolean.FALSE;
            }
            long expiryTime = expiryMap.get(key);
            boolean flag = System.currentTimeMillis() > expiryTime;
            if (flag) {
                if (isRemoveSuper) {
                    super.remove(key);
                }
                expiryMap.remove(key);
            }
            return flag;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 清除过期缓存
     */
    public void clearExpiredCache() {
        lock.lock();
        try {
            Collection<V> values = super.values();
            if (values == null || values.size() == 0) {
                return;
            }
            values.removeIf(next -> !containsValue(next));
        } finally {
            lock.unlock();
        }
    }
}