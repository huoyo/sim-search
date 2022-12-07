package cn.langpy.simsearch.util;

import cn.langpy.simsearch.model.IndexContent;
import cn.langpy.simsearch.service.IndexService;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * a full tool class for simsearch
 */
@Component
public class IndexManager implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static IndexService indexService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        IndexManager.applicationContext = applicationContext;
        indexService = getBean(IndexService.class);
    }

    public static <T> T getBean(String name) {
        if (applicationContext == null) {
            return null;
        }
        return (T) applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> c) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(c);
    }

    /**
     * create index by idName and idValue (it will delete existed index,so you can think of this method as insertOrUpdate())
     */
    public static void createIndex(IndexContent indexContent) {
        indexService.createIndex(indexContent);
    }
    public static void createIndex(Object entity) {
        ReflectUtil.checkParamValue(entity);
        IndexContent indexContent = ReflectUtil.toIndexContent(entity);
        indexService.createIndex(indexContent);
    }

    public static void createIndexs(List<Object> entities) {
        if (entities.size()==0) {
            return;
        }
        if (entities.size()<5000) {
            List<IndexContent> indexContents = new ArrayList<>();
            for (Object entity : entities) {
                ReflectUtil.checkParamValue(entity);
                IndexContent indexContent = ReflectUtil.toIndexContent(entity);
                indexContents.add(indexContent);
            }
            indexService.batchCreateIndex(indexContents);
        }else {
            List<IndexContent> indexContents = new ArrayList<>();
            for (Object entity : entities) {
                ReflectUtil.checkParamValue(entity);
                IndexContent indexContent = ReflectUtil.toIndexContent(entity);
                indexContents.add(indexContent);
                if (indexContents.size()>=5000) {
                    indexService.batchCreateIndex(indexContents);
                    indexContents.clear();
                }
            }
            if (indexContents.size()>0) {
                indexService.batchCreateIndex(indexContents);
            }
        }

    }

    /**
     * delete index  by idName and idValue
     */
    public static void deleteIndex(String idName, String idValue,Class entityClass) {
        indexService.deleteIndex(entityClass.getSimpleName(),idName, idValue);
    }

    public static List<Document> searchIndexs(String name, String value, int topn,Class entityClass) {
        return indexService.searchIndexs(entityClass.getSimpleName(),name, value, topn);
    }
    public static List<Document> searchIndexs(String name, String value) {
        return indexService.searchIndexs(name, value);
    }

    public static List<Document> searchIndexs(String name, String value,Class entityClass) {
        return searchIndexs(name, value,50,entityClass);
    }

    public static <T> List<T> searchIndexIds(String name, String value,Class<?> entityClass) {
        List<Document> docs = searchIndexs(name, value,entityClass);
        List<T> objects = ReflectUtil.transToReturnId(docs, entityClass);
        return objects;
    }
    public static <T> List<T> searchIndexObjects(String name, String value,Class entityClass) {
        List<Document> docs = searchIndexs(name, value,entityClass);
        List<T> objects = ReflectUtil.transToReturnObject(docs, entityClass);
        return objects;
    }


    public static void deleteAll() {
        indexService.deleteAll();
    }

    public static void close(IndexReader indexReader) {
        try {
            indexReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(IndexReader indexReader) {
        ResourceHook.closeOnExit(indexReader);
    }

    public static void close(IndexWriter indexWriter) {
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(IndexWriter indexWriter) {
        ResourceHook.closeOnExit(indexWriter);
    }

    public static void close(Directory directory) {
        try {
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(Directory directory) {
        ResourceHook.closeOnExit(directory);
    }

    public static void close(SearcherManager searcherManager) {
        try {
            searcherManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(SearcherManager searcherManager) {
        ResourceHook.closeOnExit(searcherManager);
    }

    public static void close(ControlledRealTimeReopenThread controlledRealTimeReopenThread) {
        try {
            controlledRealTimeReopenThread.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(ControlledRealTimeReopenThread controlledRealTimeReopenThread) {
        ResourceHook.closeOnExit(controlledRealTimeReopenThread);
    }
}
