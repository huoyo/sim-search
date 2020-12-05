package cn.langpy.simsearch.aop;

import cn.langpy.simsearch.task.IndexTask;
import org.apache.lucene.search.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Aspect
@Component
public class SearchAspect {
    @Autowired
    SearcherManager searcherManager;
    @Autowired
    IndexTask indexTask;

    @Pointcut("@annotation(cn.langpy.simsearch.annotation.SearchIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint)  throws Throwable {
        List<Object> searchResult = (List<Object>)indexTask.searchIndex(joinPoint);
        if (searchResult==null||searchResult.size()==0) {
            return joinPoint.proceed();
        }
        return indexTask.searchIndex(joinPoint);
    }

}
