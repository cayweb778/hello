package org.springbooz.webflux;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @author 木子桉易洋
 * Date: 11:29
 * Time: 2018/8/10
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BoozContext {
  String value() default "users";
}
