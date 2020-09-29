package cn.langpy.simsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @name：索引id
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexId {
    boolean isIndex() default true;
}
