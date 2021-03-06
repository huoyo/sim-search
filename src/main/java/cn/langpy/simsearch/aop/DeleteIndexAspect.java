package cn.langpy.simsearch.aop;

import cn.langpy.simsearch.task.IndexTask;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
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
