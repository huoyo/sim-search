package com.huoyo.luceneannotation.aop;

import com.huoyo.luceneannotation.annotation.CreateIndex;
import com.huoyo.luceneannotation.annotation.IndexColumn;
import com.huoyo.luceneannotation.annotation.IndexId;
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
import org.springframework.core.annotation.AnnotationUtils;
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
public class IndexAspect {
    @Autowired
    IndexWriter indexWriter;

    @Pointcut("@annotation(com.huoyo.luceneannotation.annotation.CreateIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint) throws Throwable {
        CreateIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CreateIndex.class);
        Object re = joinPoint.proceed();
        /*异步创建索引*/
        new Thread(()-> {
            createEntityIndex(joinPoint,createIndex);
        }).start();
        return re;
    }


    public void createEntityIndex(ProceedingJoinPoint joinPoint,CreateIndex createIndex) {
        Object[] params = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        String index = StringUtils.isEmpty(createIndex.indexParam())?paramNames[0]:createIndex.indexParam();
        for (int i = 0; i <paramNames.length ; i++) {
            if (index.equals(paramNames[i])) {
                Object arg = params[i];
                String indexIdColumn = "";
                Object indexIdValue = "";
                Field[] fields = arg.getClass().getDeclaredFields();
                Document doc = new Document();
                for (int j = 0; j < fields.length; j++) {
                    IndexId indexId = fields[j].getAnnotation(IndexId.class);
                    if (indexId!=null&& StringUtils.isEmpty(indexIdValue)) {
                        indexIdColumn = fields[j].getName();
                        PropertyDescriptor columnIdPd = null;
                        try {
                            columnIdPd = new PropertyDescriptor(indexIdColumn, arg.getClass());
                            Method idMethod = columnIdPd.getReadMethod();
                            indexIdValue = idMethod.invoke(arg);
                            indexWriter.deleteDocuments(new Term(indexIdColumn,indexIdValue+""));
                            doc.add(new StringField(indexIdColumn, indexIdValue+"", org.apache.lucene.document.Field.Store.YES));
                            continue;
                        } catch (IntrospectionException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    IndexColumn indexColumn = AnnotationUtils.findAnnotation(fields[j],IndexColumn.class);
                    if (indexColumn!=null) {
                        String indexNameColumn = fields[j].getName();
                        PropertyDescriptor columnPd = null;
                        try {
                            columnPd = new PropertyDescriptor(indexNameColumn, arg.getClass());
                            Method columnMethod = columnPd.getReadMethod();
                            Object indexNameValue = columnMethod.invoke(arg);
                            doc.add(new TextField(indexNameColumn, indexNameValue+"", org.apache.lucene.document.Field.Store.YES));
                        } catch (IntrospectionException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    indexWriter.addDocument(doc);
                    indexWriter.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
