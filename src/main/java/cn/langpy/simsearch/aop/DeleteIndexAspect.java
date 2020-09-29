package cn.langpy.simsearch.aop;

import cn.langpy.simsearch.task.IndexTask;
import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @name：创建索引注解实现过程
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Aspect
@Component
@Log
public class DeleteIndexAspect {

    @Autowired
    IndexTask indexTask;

    @Pointcut("@annotation(cn.langpy.simsearch.annotation.DeleteIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint) throws Throwable {
        Object re = joinPoint.proceed();
        indexTask.deleteIndex(joinPoint);
        return re;
    }

}
