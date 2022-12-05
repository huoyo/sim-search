package cn.langpy.simsearch.intercepter;

import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.AopService;
import cn.langpy.simsearch.service.IndexService;
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
    AopService aopService;
    @Autowired
    IndexService indexService;

    @Pointcut("@annotation(cn.langpy.simsearch.annotation.DeleteIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint) throws Throwable {
        Object re = joinPoint.proceed();
        IndexItem indexContent = aopService.getDeleteItem(joinPoint);
        indexService.deleteIndex(indexContent.getName(),indexContent.getValue());
        return re;
    }

}
