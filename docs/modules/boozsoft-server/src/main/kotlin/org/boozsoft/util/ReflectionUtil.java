package org.boozsoft.util;

import java.lang.reflect.Field;

/**
 *
 * 通过反射操作对象属性
 *
 * @author lz
 */
public class ReflectionUtil {

    private ReflectionUtil() {
        throw new AssertionError();
    }

    /**
     * 通过反射取对象指定属性的值
     * @param targetOb 目标对象
     * @param fieldName 属性名字
     * @return 属性的值
     */
    public static Object getValue(Object targetOb, String fieldName) {
        //获取类对象
        Class<?> clazz = targetOb.getClass();
        Field field;
        try {
            //通过类对象拿到字段对象
            field = clazz.getDeclaredField(fieldName);
            //将字段对象设置为可访问
            field.setAccessible(true);
            //取出指定字段的值
            targetOb = field.get(targetOb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetOb;
    }

    /**
     * 通过反射对象的指定属性赋值
     * @param targetOb 目标对象
     * @param fieldName 属性名字
     * @param value 值
     */
    public static void setValue(Object targetOb, String fieldName, Object value) {
        //获取类对象
        Class<?> clazz = targetOb.getClass();
        Field field;
        try {
            //通过类对象拿到字段对象
            field = clazz.getDeclaredField(fieldName);
            //将字段对象设置为可访问
            field.setAccessible(true);
            //给指定字段设值
            field.set(targetOb, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
