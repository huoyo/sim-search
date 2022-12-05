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
import java.util.List;

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
     * create index by idName and idValue (it will delete existed index)
     */
    public static void createIndex(IndexContent indexContent) {
        indexService.createIndex(indexContent);
    }

    /**
     * delete index  by idName and idValue
     */
    public static void deleteIndex(String idName, String idValue) {
        indexService.deleteIndex(idName, idValue);
    }

    public static List<Document> searchIndex(String name, String value, int topn) {
        return indexService.searchIndex(name, value, topn);
    }

    public static List<Document> searchIndex(String name, String value) {
        return searchIndex(name, value);
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close(indexReader);
        }));
    }

    public static void close(IndexWriter indexWriter) {
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(IndexWriter indexWriter) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close(indexWriter);
        }));
    }

    public static void close(Directory directory) {
        try {
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(Directory directory) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close(directory);
        }));
    }

    public static void close(SearcherManager searcherManager) {
        try {
            searcherManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(SearcherManager searcherManager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close(searcherManager);
        }));
    }

    public static void close(ControlledRealTimeReopenThread controlledRealTimeReopenThread) {
        try {
            controlledRealTimeReopenThread.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeOnExit(ControlledRealTimeReopenThread controlledRealTimeReopenThread) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close(controlledRealTimeReopenThread);
        }));
    }
}
