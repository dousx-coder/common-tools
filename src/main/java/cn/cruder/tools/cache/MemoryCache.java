package cn.cruder.tools.cache;

import cn.hutool.log.Log;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 基于内存实现的过期时间Map(线程安全)
 * <p/>
 *
 * @author dousx
 */
public class MemoryCache<K, V> implements Serializable {
    private static final Log log = Log.get(MemoryCache.class);
    private static final Long DURATION_DEFAULT_VALUE = 3600L;
    private static final Integer MAXIMUM_SIZE_DEFAULT_VALUE = 2048;

    /**
     * 用于定时清除已过期缓存
     */
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * 同步锁
     */
    private final Lock lock = new ReentrantLock();

    /**
     * 缓存(线程安全)
     */
    private LoadingCache<K, V> cache;

    /**
     * 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
     */
    private Integer maximumSize;

    /**
     * 过期时间(距离最后一次访问时间)
     * <br/>
     * 单位{@link MemoryCache#unit}
     */
    private Long duration;

    /**
     * 过期时间单位
     * {@link cn.cruder.tools.cache.MemoryCache#duration}
     */
    private TimeUnit unit;


    /**
     * 默认值,即当一个key超过3600秒没有访问时,会被清除
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#maximumSize} = 2048
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#duration} = 3600
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#unit} = {@link TimeUnit#SECONDS}
     */
    public MemoryCache() {
        this(DURATION_DEFAULT_VALUE, TimeUnit.SECONDS);
    }

    /**
     * 单位 {@link TimeUnit#SECONDS}
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#maximumSize} = 2048
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#unit} = {@link TimeUnit#SECONDS}
     *
     * @param duration 过期时间(距离最后一次访问时间)
     */
    public MemoryCache(long duration) {
        this(MAXIMUM_SIZE_DEFAULT_VALUE, duration, TimeUnit.SECONDS);
    }

    /**
     * 单位 {@link TimeUnit#SECONDS}
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#maximumSize} = 2048
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#unit} = {@link TimeUnit#SECONDS}
     *
     * @param cache    自定义缓存
     * @param duration 过期时间(距离最后一次访问时间)
     */
    public MemoryCache(LoadingCache<K, V> cache, Long duration) {
        this(cache, MAXIMUM_SIZE_DEFAULT_VALUE, duration);
    }

    /**
     * 单位 {@link TimeUnit#SECONDS}
     *
     * @param cache       自定义缓存
     * @param maximumSize 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
     * @param duration    过期时间(距离最后一次访问时间)
     */
    public MemoryCache(LoadingCache<K, V> cache, Integer maximumSize, Long duration) {
        this(cache, maximumSize, duration, TimeUnit.SECONDS);
    }

    /**
     * 单位 {@link TimeUnit#SECONDS}
     *
     * @param maximumSize 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
     * @param duration    过期时间(距离最后一次访问时间)
     */
    public MemoryCache(Integer maximumSize, long duration) {
        this(maximumSize, duration, TimeUnit.SECONDS);
    }

    /**
     * <li/> {@link cn.cruder.tools.cache.MemoryCache#maximumSize} = 2048
     *
     * @param duration 过期时间(距离最后一次访问时间)
     * @param unit     过期时间单位
     */
    private MemoryCache(long duration, TimeUnit unit) {
        this(MAXIMUM_SIZE_DEFAULT_VALUE, duration, unit);
    }


    /**
     * @param maximumSize 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
     * @param duration    过期时间(距离最后一次访问时间)
     * @param unit        过期时间单位
     */
    private MemoryCache(Integer maximumSize, long duration, TimeUnit unit) {
        this((LoadingCache<K, V>) CacheBuilder.newBuilder()
                //缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
                .maximumSize(maximumSize)
                //设置时间对象没有被读/写访问则对象从内存中删除(在另外的线程里面不定期维护)
                .expireAfterAccess(duration, unit)
                //开启Guava Cache的统计功能
                .recordStats()
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) {
                        return null;
                    }
                }), maximumSize, duration, unit);
    }

    /**
     * @param cache       自定义缓存
     * @param maximumSize 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
     * @param duration    过期时间(距离最后一次访问时间)
     * @param unit        过期时间单位
     */
    private MemoryCache(LoadingCache<K, V> cache, Integer maximumSize, long duration, TimeUnit unit) {
        this.cache = cache;
        this.maximumSize = maximumSize;
        this.duration = duration;
        this.unit = unit;
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (cache == null || cache.size() == 0) {
                return;
            }
            lock.lock();
            try {
                log.trace("cache.cleanUp(),{}", cache);
                cache.cleanUp();
            } catch (Exception e) {
                log.error("定时清除过期缓存异常,cache {}", cache, e);
            } finally {
                lock.unlock();
            }
        }, 0, (duration / 2) + 1, unit);
    }


    /**
     * 获取值
     *
     * @param key key
     * @return value
     */
    public V get(K key) {
        V v = null;
        lock.lock();
        try {
            this.cache.cleanUp();
            v = this.cache.get(key);
        } catch (Exception e) {
            log.trace("加载key:{} 异常", key, e);
        } finally {
            lock.unlock();
        }
        return v;
    }


    /**
     * 存入值
     *
     * @param key   key
     * @param value value
     */
    public void put(K key, V value) {
        lock.lock();
        try {
            this.cache.put(key, value);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 缓存大小
     *
     * @return 缓存大小
     */
    public Long size() {
        lock.lock();
        try {
            return this.cache.size();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 丢弃键key的缓存值
     *
     * @param key key
     */
    public void invalidate(K key) {
        lock.lock();
        try {
            this.cache.invalidate(key);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 清空缓存
     */
    public void invalidateAll() {
        lock.lock();
        try {
            this.cache.invalidateAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        scheduledExecutorService.shutdown();
    }
}