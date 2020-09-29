package com.langpy.simsearch.aop;

import com.langpy.simsearch.annotation.SearchIndex;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @name：索引所搜实现过程
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:14
 */
@Aspect
@Component
public class SearchAspect {
    @Autowired
    SearcherManager searcherManager;

    @Pointcut("@annotation(com.langpy.simsearch.annotation.SearchIndex)")
    public void preProcess(){

    }
    @Around("preProcess()")
    public Object  before(ProceedingJoinPoint joinPoint)  {
        return searchEntityIndex(joinPoint);
    }

    public Object searchEntityIndex(ProceedingJoinPoint joinPoint) {
        SearchIndex searchIndex = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(SearchIndex.class);
        Object[] params = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        String indexName = StringUtils.isEmpty(searchIndex.by())?paramNames[0]:searchIndex.by();
        Class returnType = searchIndex.searchEntity();
        List<Object> documents = new ArrayList<>();
        Object arg = params[0];
        IndexSearcher indexSearcher = null;
        try {
            searcherManager.maybeRefresh();
            indexSearcher = searcherManager.acquire();
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryParser parser = new QueryParser(indexName, new StandardAnalyzer());
        Query query = null;
        TopDocs topDocs = null;
        try {
            query = parser.parse(arg+"");
            topDocs = indexSearcher.search(query, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc = null;
            try {
                doc = indexSearcher.doc(docID);
                Object re = returnType.newInstance();
                for(Field field:returnType.getDeclaredFields()){
                    String fieldName = field.getName();
                    PropertyDescriptor columnPd = new PropertyDescriptor(fieldName, returnType);
                    Method columnMethod = columnPd.getWriteMethod();
                    columnMethod.invoke(re,doc.get(fieldName));
                }
                documents.add(re);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return documents;
    }
}
