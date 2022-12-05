package cn.langpy.simsearch.service;


import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import org.aspectj.lang.ProceedingJoinPoint;

public interface AopService {

    IndexContent getIndexContent(ProceedingJoinPoint joinPoint);

    IndexItem getDeleteItem(ProceedingJoinPoint joinPoint);

    IndexItem getSearchItem(ProceedingJoinPoint joinPoint);

    Class<?> getReturnClass(ProceedingJoinPoint joinPoint);

}
