package com.langpy.simsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @name：删除索引注解
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteIndex {
    String indexParam() default "";
}
