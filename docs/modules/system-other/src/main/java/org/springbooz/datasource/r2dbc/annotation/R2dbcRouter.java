package org.springbooz.datasource.r2dbc.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface R2dbcRouter {
    /**
     * redis下可用,需配置
     * <pre>
     * spring:
     *    booz-jpa:
     *        router:
     *            enable: true
     *            context: redis
     * </pre>
     */


    /**
     * 模式名
     * @return
     */
    String schemaName() default "";

    /**
     * 数据库名
     * @return
     */
    String databaseName() default "";

    /**
     * 模式选项
     * @return
     */
    SCHEMA_TYPE[] value() default {};

    boolean txManger() default false;
}
