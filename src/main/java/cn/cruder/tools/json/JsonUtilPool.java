package cn.cruder.tools.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author dousx
 * @date 2022-05-08 13:23
 */
public class JsonUtilPool {
    private JsonUtilPool() {
    }

    /**
     * 获取随机数
     */
    private static final Random RANDOM = new Random();

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
        return GSON_INSTANCE_POOL.get(RANDOM.nextInt() % SIZE);
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return {@link ObjectMapper}
     */
    public synchronized static ObjectMapper getObjectMapperInstancePool() {
        return OBJECT_MAPPER_INSTANCE_POOL.get(RANDOM.nextInt() % SIZE);
    }

}
