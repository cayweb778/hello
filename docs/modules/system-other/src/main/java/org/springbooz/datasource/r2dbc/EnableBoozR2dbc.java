package org.springbooz.datasource.r2dbc;//package org.springbooz.datasource.r2dbc;
//
//import org.springbooz.datasource.r2dbc.annotation.DynamicDataSourceAop;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.annotation.AliasFor;
//import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
//
//import java.lang.annotation.Inherited;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//
///**
// * @author 木子桉易洋
// * @version 1.0
// * @company 财税达软件科技
// * @date 2020/12/30 2:06 下午
// */
//
//@Import({
//        DynamicDataSourceAop.class,
//        BoozR2dbcConfiguration.class,
//        BoozR2dbcAutoConfiguration.class
//})
//@Retention(RetentionPolicy.RUNTIME)
//@EnableR2dbcRepositories
//@Inherited
//public @interface EnableBoozR2dbc {
//    @AliasFor(annotation = EnableR2dbcRepositories.class)
//    String[] value() default {};
//}
