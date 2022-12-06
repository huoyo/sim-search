package cn.langpy.simsearch.intercepter;

import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.AopService;
import cn.langpy.simsearch.service.IndexService;
import cn.langpy.simsearch.util.ReflectUtil;
import org.apache.lucene.document.Document;
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
    AopService aopService;
    @Autowired
    IndexService indexService;

    @Pointcut("@annotation(cn.langpy.simsearch.annotation.SearchIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  around(ProceedingJoinPoint joinPoint)  throws Throwable {
        IndexItem indexContent = aopService.getSearchItem(joinPoint);
        List<Document> documents = indexService.searchIndexs(indexContent.getEntitySource().getSimpleName(),indexContent.getName(), indexContent.getValue());
        if (documents.size()==0) {
            return joinPoint.proceed();
        }
        List<?> objects = ReflectUtil.transToReturnObject(documents, indexContent.getEntitySource());
        return objects;
    }

}
