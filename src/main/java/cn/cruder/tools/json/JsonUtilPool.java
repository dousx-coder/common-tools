package cn.cruder.tools.json;

import cn.hutool.log.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dousx
 * @date 2022-05-08 13:23
 */
public class JsonUtilPool {
    private JsonUtilPool() {
    }

    private static final Log log = Log.get(JsonUtilPool.class);

    /**
     * 轮询计数器
     */
    private static final AtomicInteger INDEX_ATOMIC = new AtomicInteger(0);


    /**
     * Gson池大小
     */
    private static final Integer SIZE = 10;

    /**
     * <a href="https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse">Gson是线程安全的</a>
     */
    private static final List<Gson> GSON_INSTANCE_POOL = new LinkedList<>();


    /**
     * <a href="https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field">ObjectMapper是线程安全的</a>
     */
    private static final List<ObjectMapper> OBJECT_MAPPER_INSTANCE_POOL = new LinkedList<>();

    static {
        for (int i = 0; i < SIZE; i++) {
            GSON_INSTANCE_POOL.add(gson());
        }
        for (int i = 0; i < SIZE; i++) {
            OBJECT_MAPPER_INSTANCE_POOL.add(objectMapper());
        }
    }


    /**
     * 构造Gson实例
     *
     * @return {@link Gson}
     */
    private static Gson gson() {
        return new Gson();
    }

    /**
     * 构造ObjectMapper实例
     *
     * @return {@link ObjectMapper}
     */
    private static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    /**
     * 获取Gson实例
     *
     * @return {@link Gson}
     */
    public synchronized static Gson getGsonInstance() {
        return GSON_INSTANCE_POOL.get(getIndex());
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return {@link ObjectMapper}
     */
    public synchronized static ObjectMapper getObjectMapperInstancePool() {
        return OBJECT_MAPPER_INSTANCE_POOL.get(getIndex());
    }

    /**
     * 轮询获取下标
     *
     * @return 下标
     */
    private synchronized static int getIndex() {
        int index = INDEX_ATOMIC.getAndIncrement() % SIZE;
        if (INDEX_ATOMIC.get() >= SIZE) {
            // 重新计数
            INDEX_ATOMIC.set(0);
        }
        return index;
    }


}
