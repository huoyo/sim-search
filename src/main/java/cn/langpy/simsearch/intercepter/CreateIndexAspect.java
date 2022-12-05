package cn.langpy.simsearch.intercepter;

import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.service.AopService;
import cn.langpy.simsearch.service.IndexService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Aspect
@Component
public class CreateIndexAspect {
    @Resource(name = "indexExecutor")
    ThreadPoolTaskExecutor executor;
    @Autowired
    AopService aopService;
    @Autowired
    IndexService indexService;

    @Pointcut("@annotation(cn.langpy.simsearch.annotation.CreateIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object re = joinPoint.proceed();
        executor.submit(()->{
            IndexContent indexContent = aopService.getIndexContent(joinPoint);
            indexService.createIndex(indexContent);
        });
        return re;
    }

}
