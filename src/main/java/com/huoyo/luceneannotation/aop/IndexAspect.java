package com.huoyo.luceneannotation.aop;

import com.huoyo.luceneannotation.annotation.CreateIndex;
import com.huoyo.luceneannotation.annotation.IndexColumn;
import com.huoyo.luceneannotation.annotation.IndexId;
import com.huoyo.luceneannotation.task.IndexTask;
import lombok.extern.java.Log;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @name：创建索引注解实现过程
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Aspect
@Component
@Log
public class IndexAspect {

    @Autowired
    IndexTask indexTask;

    @Pointcut("@annotation(com.huoyo.luceneannotation.annotation.CreateIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint) throws Throwable {
        Object re = joinPoint.proceed();
        indexTask.createIndex(joinPoint);
        return re;
    }

}
