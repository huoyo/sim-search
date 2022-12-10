package cn.langpy.simsearch.service.impl;

import cn.langpy.simsearch.annotation.*;
import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.model.IndexItem;
import cn.langpy.simsearch.service.AopService;
import cn.langpy.simsearch.util.ReflectUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Service
public class DefaultAopService implements AopService {

    public String getIndexParam(String indexParam, String[] paramNames, Method method) {
        if (StringUtils.hasText(indexParam)) {
            if (paramNames.length == 0) {
                throw new RuntimeException("can not create index for method " + method.getName() + ",cause it has not any parameter");
            }
            indexParam = paramNames[0];
        }
        return indexParam;
    }

    public Object getIndexParamValue(String[] paramNames, Object[] paramValues, String indexParamName, Method method) {
        int indexParamIndex = -1;
        for (int i = 0; i < paramNames.length; i++) {
            if (indexParamName.equals(paramNames[i])) {
                indexParamIndex = i;
                break;
            }
        }
        if (indexParamIndex == -1) {
            throw new RuntimeException("error indexParam in @CreateIndex on method " + method.getName());
        }
        Object indexParamValue = paramValues[indexParamIndex];
        return indexParamValue;
    }


    @Override
    public IndexContent getIndexContent(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        CreateIndex createIndex = getAnnotation(method,CreateIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);
        Object[] paramValues = joinPoint.getArgs();
        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        ReflectUtil.checkParamValue(indexParamValue);
        IndexContent indexContent = ReflectUtil.toIndexContent(indexParamValue);
        return indexContent;
    }

    @Override
    public IndexItem getDeleteItem(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        DeleteIndex createIndex = getAnnotation(method,DeleteIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);
        Object[] paramValues = joinPoint.getArgs();

        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        ReflectUtil.checkParamValue(indexParamValue);
        Field[] fields = indexParamValue.getClass().getDeclaredFields();
        IndexItem indexContent = new IndexItem();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            IndexId indexId = field.getAnnotation(IndexId.class);
            if (indexId != null) {
                Object indexIdColumnValue = ReflectUtil.getFieldValue(field, indexParamValue);
                indexContent.setName(field.getName());
                indexContent.setValue(indexIdColumnValue + "");
                break;
            }
        }
        indexContent.setEntitySource(indexParamValue.getClass());
        return indexContent;
    }

    @Override
    public IndexItem getSearchItem(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        SearchIndex createIndex = getAnnotation(method,SearchIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);

        Object[] paramValues = joinPoint.getArgs();
        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        ReflectUtil.checkParamValue(indexParamValue);
        String searchColumnName = createIndex.by();
        if (StringUtils.hasText(searchColumnName)) {
            throw new RuntimeException("error by in @SearchIndex on method " + method.getName());
        }

        Field[] fields = indexParamValue.getClass().getDeclaredFields();
        IndexItem indexContent = new IndexItem();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (searchColumnName.equals(field.getName())) {
                Object indexIdColumnValue = ReflectUtil.getFieldValue(field, indexParamValue);
                indexContent.setName(field.getName());
                indexContent.setValue(indexIdColumnValue + "");
            }
        }
        indexContent.setEntitySource(indexParamValue.getClass());
        return indexContent;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return method;
    }

    private  <T extends Annotation> T getAnnotation(Method method,Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    @Override
    public Class<?> getReturnClass(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return method.getReturnType();
    }
}
