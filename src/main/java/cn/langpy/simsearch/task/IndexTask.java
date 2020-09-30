package cn.langpy.simsearch.task;

import cn.langpy.simsearch.annotation.CreateIndex;
import cn.langpy.simsearch.annotation.DeleteIndex;
import cn.langpy.simsearch.annotation.IndexColumn;
import cn.langpy.simsearch.annotation.IndexId;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @name：
 * @function：
 * @author：zhangchang
 * @date 2020/9/28 16:27
 */
@Service
public class IndexTask {
    @Autowired
    IndexWriter indexWriter;

    @Async(value = "indexExecutor")
    public void createIndex(ProceedingJoinPoint joinPoint) {
        CreateIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(CreateIndex.class);
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
                            doc.add(new StringField(indexIdColumn, indexIdValue+"", org.apache.lucene.document.Field.Store.YES));
                            continue;
                        } catch (IntrospectionException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
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
                    indexWriter.deleteDocuments(new Term(indexIdColumn,indexIdValue+""));
                    indexWriter.addDocument(doc);
                    indexWriter.commit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Async(value = "indexExecutor")
    public void deleteIndex(ProceedingJoinPoint joinPoint) {
        DeleteIndex createIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DeleteIndex.class);
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
                            indexWriter.addDocument(doc);
                            indexWriter.commit();
                            break;
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
                }
            }
        }
    }
}
