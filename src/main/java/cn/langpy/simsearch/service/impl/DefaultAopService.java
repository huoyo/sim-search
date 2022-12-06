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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DefaultAopService implements AopService {
    private final static List<Class<?>> baseTypes = Arrays.asList(Integer.class, Double.class, Float.class, String.class, Boolean.class, List.class, Math.class);

    public String getIndexParam(String indexParam, String[] paramNames, Method method) {
        if (StringUtils.isEmpty(indexParam)) {
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

    public void checkParamValue(Object indexParamValue) {
        Class<?> aClass = indexParamValue.getClass();
        if (baseTypes.contains(aClass)) {
            throw new RuntimeException("can not create index for base types:" + baseTypes);
        }
    }

    @Override
    public IndexContent getIndexContent(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        CreateIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CreateIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);
        Object[] paramValues = joinPoint.getArgs();
        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        checkParamValue(indexParamValue);
        Field[] fields = indexParamValue.getClass().getDeclaredFields();
        IndexContent indexContent = new IndexContent();
        List<IndexItem> indexItems = new ArrayList<>();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            IndexId indexId = field.getAnnotation(IndexId.class);
            if (indexId != null) {
                Object indexIdColumnValue = ReflectUtil.getFieldValue(field);
                indexContent.setIdName(field.getName());
                indexContent.setIdValue(indexIdColumnValue + "");
                continue;
            }
            IndexColumn indexColumn = field.getAnnotation(IndexColumn.class);
            if (indexColumn != null) {
                Object indexColumnValue = ReflectUtil.getFieldValue(field);
                IndexItem indexItem = new IndexItem();
                indexItem.setName(field.getName());
                indexItem.setValue(indexColumnValue + "");
                indexItems.add(indexItem);
            }
        }
        indexContent.setItems(indexItems);
        return indexContent;
    }

    @Override
    public IndexItem getDeleteItem(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DeleteIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DeleteIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);
        Object[] paramValues = joinPoint.getArgs();

        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        checkParamValue(indexParamValue);
        Field[] fields = indexParamValue.getClass().getDeclaredFields();
        IndexItem indexContent = new IndexItem();
        for (int j = 0; j < fields.length; j++) {
            Field field = fields[j];
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            IndexId indexId = field.getAnnotation(IndexId.class);
            if (indexId != null) {
                Object indexIdColumnValue = ReflectUtil.getFieldValue(field);
                indexContent.setName(field.getName());
                indexContent.setValue(indexIdColumnValue + "");
                break;
            }
        }
        return indexContent;
    }

    @Override
    public IndexItem getSearchItem(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SearchIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(SearchIndex.class);
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        String indexParamName = getIndexParam(createIndex.indexParam(), paramNames, method);

        Object[] paramValues = joinPoint.getArgs();
        Object indexParamValue = getIndexParamValue(paramNames, paramValues, indexParamName, method);
        checkParamValue(indexParamValue);
        String searchColumnName = createIndex.by();
        if (StringUtils.isEmpty(searchColumnName)) {
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
                Object indexIdColumnValue = ReflectUtil.getFieldValue(field);
                indexContent.setName(field.getName());
                indexContent.setValue(indexIdColumnValue + "");
            }
        }
        return indexContent;
    }

    @Override
    public Class<?> getReturnClass(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return method.getReturnType();
    }
}
