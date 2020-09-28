package com.huoyo.luceneannotation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @name：索引搜索注解
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchIndex {
    String by() default "";
    Class searchEntity() ;
}
