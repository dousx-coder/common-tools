package io.github.dousxcoder.tools.json;

import cn.hutool.core.date.DatePattern;
import cn.hutool.log.Log;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dousx
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
     * PollSize
     */
    private static final Integer POLL_SIZE = 10;

    /**
     * 重置条件
     */
    private static final Integer RESET_CONDITION = Integer.MAX_VALUE / 100;

    /**
     * <a href="https://stackoverflow.com/questions/10380835/is-it-ok-to-use-gson-instance-as-a-static-field-in-a-model-bean-reuse">Gson是线程安全的</a>
     */
    private static final List<Gson> GSON_INSTANCE_POOL = new LinkedList<>();
    /**
     * 指定日期格式
     */
    private static final Map<String, Gson> DATE_FORMAT_GSON_POOL = new ConcurrentHashMap<>();
    private static final Map<String, ObjectMapper> DATE_FORMAT_OBJECT_MAPPER_INSTANCE_POOL = new ConcurrentHashMap<>();


    /**
     * <a href="https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field">ObjectMapper是线程安全的</a>
     */
    private static final List<ObjectMapper> OBJECT_MAPPER_INSTANCE_POOL = new LinkedList<>();

    static {
        for (int i = 0; i < POLL_SIZE; i++) {
            GSON_INSTANCE_POOL.add(gson());
        }
        for (int i = 0; i < POLL_SIZE; i++) {
            OBJECT_MAPPER_INSTANCE_POOL.add(objectMapper());
        }
        ArrayList<String> dateFormatList = new ArrayList<>();
        // 初始化常见格式
        dateFormatList.add(DatePattern.NORM_DATETIME_PATTERN);
        dateFormatList.add(DatePattern.NORM_DATETIME_MS_PATTERN);
        dateFormatList.add(DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        dateFormatList.add(DatePattern.NORM_DATE_PATTERN);

        for (String dateFormat : dateFormatList) {
            gsonWithDateFormat(dateFormat);
            objectMapperWithDateFormat(dateFormat);
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
     * 获取Gson实例
     *
     * @return {@link Gson}
     */
    public synchronized static Gson getDateFormatGsonInstance() {
        return GSON_INSTANCE_POOL.get(getIndex());
    }

    /**
     * 获取ObjectMapper实例
     *
     * @return {@link ObjectMapper}
     */
    public synchronized static ObjectMapper getObjectMapperInstance() {
        return OBJECT_MAPPER_INSTANCE_POOL.get(getIndex());
    }

    /**
     * 轮询获取下标
     *
     * @return 下标
     */
    private synchronized static int getIndex() {
        int andIncrement = INDEX_ATOMIC.getAndIncrement();
        if (andIncrement >= RESET_CONDITION) {
            // 重新计数
            INDEX_ATOMIC.set(0);
        }
        return andIncrement % POLL_SIZE;
    }

    /**
     * 每个dateFormat对应一个单例
     *
     * @param dateFormat 日期格式
     * @return {@link Gson}
     */
    public static Gson gsonWithDateFormat(String dateFormat) {
        Gson gson = DATE_FORMAT_GSON_POOL.get(dateFormat);
        if (Objects.isNull(gson)) {
            synchronized (JsonUtilPool.class) {
                gson = DATE_FORMAT_GSON_POOL.get(dateFormat);
                if (Objects.isNull(gson)) {
                    log.debug("new GsonBuilder().setDateFormat({}).create()", dateFormat);
                    gson = new GsonBuilder().setDateFormat(dateFormat).create();
                    DATE_FORMAT_GSON_POOL.put(dateFormat, gson);
                }
            }
        }
        return gson;
    }


    /**
     * 每个dateFormat对应一个单例
     *
     * @param dateFormat 日期格式
     * @return {@link ObjectMapper}
     */
    public static ObjectMapper objectMapperWithDateFormat(String dateFormat) {
        ObjectMapper objectMapper = DATE_FORMAT_OBJECT_MAPPER_INSTANCE_POOL.get(dateFormat);
        if (Objects.isNull(objectMapper)) {
            synchronized (JsonUtilPool.class) {
                objectMapper = DATE_FORMAT_OBJECT_MAPPER_INSTANCE_POOL.get(dateFormat);
                if (Objects.isNull(objectMapper)) {
                    log.debug("new GsonBuilder().setDateFormat({}).create()", dateFormat);
                    objectMapper = new ObjectMapper();
                    objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
                    DATE_FORMAT_OBJECT_MAPPER_INSTANCE_POOL.put(dateFormat, objectMapper);
                }
            }
        }
        return objectMapper;
    }

    /**
     * @param json  要反序列化对象的字符串
     * @param clazz Class
     * @param <T>   t
     * @return T类型对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            throw new IllegalArgumentException();
        }
        T result = null;
        try {
            result = JSONObject.parseObject(json, clazz);
        } catch (Throwable e) {
            try {
                log.warn("反序列化失败[com.alibaba.fastjson.JSON] - {}", e.getMessage());
                result = JsonUtilPool.getObjectMapperInstance().readValue(json, clazz);
            } catch (Throwable ex) {
                try {
                    log.warn("反序列化失败[com.fasterxml.jackson.databind.ObjectMapper] - {}", ex.getMessage());
                    result = JsonUtilPool.getGsonInstance().fromJson(json, clazz);
                } catch (Throwable exc) {
                    log.warn("反序列化失败[com.google.gson.Gson] - {}", exc.getMessage());
                    throw exc;
                }
            }
        }
        return result;
    }

    /**
     * 解析list类型的json字符串
     *
     * @param json  要反序列化对象的字符串
     * @param clazz Class
     * @param <T>   t
     * @return T类型对象
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        List<T> result = null;
        try {
            result = JsonUtilPool.gson().fromJson(json, new TypeToken<List<T>>() {
            }.getType());
        } catch (Throwable ex) {
            try {
                log.warn("反序列化失败[com.google.gson.Gson] - {}", ex.getMessage());
                result = JsonUtilPool.objectMapper().readValue(json, new TypeReference<List<T>>() {
                });
            } catch (Throwable exc) {
                log.warn("反序列化失败[com.fasterxml.jackson.databind.ObjectMapper] - {}", exc.getMessage());
            }
        }
        return result;
    }


    /**
     * 对象转json
     * <br>
     * gson对于嵌套对象会报stackoverflow
     *
     * @param obj obj
     * @return json字符串
     */
    public static String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return "{}";
        }
        String result = null;
        try {
            result = JSONObject.toJSONString(obj);
        } catch (Throwable e) {
            try {
                log.warn("对象转Json字符串失败[com.alibaba.fastjson.JSON] - {}", e.getMessage());
                result = JsonUtilPool.getObjectMapperInstance().writeValueAsString(obj);
            } catch (Throwable ex) {
                try {
                    log.warn("对象转Json字符串失败[com.fasterxml.jackson.databind.ObjectMapper] - {}", ex.getMessage());
                    result = JsonUtilPool.getGsonInstance().toJson(obj);
                } catch (Throwable exc) {
                    log.warn("对象转Json字符串失败[com.google.gson.Gson] - {}", exc.getMessage());
                    throw exc;
                }
            }
        }
        return result;
    }


    public static String toJsonStringWithNormDateTimeMs(Object obj) {
        return toJsonStringWithDateFormat(obj, DatePattern.NORM_DATETIME_MS_PATTERN);
    }

    /**
     * 对象转json
     * <br>
     * gson对于嵌套对象会报stackoverflow
     *
     * @param obj        obj
     * @param dateFormat dateFormat
     * @return json字符串
     */
    public static String toJsonStringWithDateFormat(Object obj, String dateFormat) {
        if (Objects.isNull(obj)) {
            return "{}";
        }
        String result = null;
        try {
            result = JSON.toJSONString(obj, dateFormat);
        } catch (Throwable e) {
            try {
                log.warn("对象转Json字符串失败[com.alibaba.fastjson.JSON] - {}", e.getMessage());
                result = JsonUtilPool.objectMapperWithDateFormat(dateFormat).writeValueAsString(obj);
            } catch (Throwable ex) {
                try {
                    log.warn("对象转Json字符串失败[com.fasterxml.jackson.databind.ObjectMapper] - {}", ex.getMessage());
                    Gson gson = JsonUtilPool.gsonWithDateFormat(dateFormat);
                    result = gson.toJson(obj);
                } catch (Throwable exc) {
                    log.warn("对象转Json字符串失败[com.google.gson.Gson] - {}", exc.getMessage());
                    throw exc;
                }
            }
        }
        return result;
    }


}
