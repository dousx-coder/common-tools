package cn.cruder.tools.reflect;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 反射工具类
 *
 * @author dousx
 * @date 2022-05-08 18:13
 */
public class ReflectUtils {
    /**
     * 反射获取字段
     *
     * @param object Obj
     * @return 字段列表
     */
    public static Map<String, Field> getAllFields(Object object) {
        Map<String, Field> result = new HashMap<>();
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        for (Field field : fieldList) {
            String name = field.getName();
            result.put(name, field);
        }
        return result;
    }
}
