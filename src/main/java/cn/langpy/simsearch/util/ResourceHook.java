package cn.langpy.simsearch.util;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public class ResourceHook {
    public static Logger log = Logger.getLogger(ResourceHook.class.toString());

    private static LinkedHashSet<IndexReader> indexReaders = new LinkedHashSet<>();
    private static LinkedHashSet<IndexWriter> indexWriters = new LinkedHashSet<>();
    private static LinkedHashSet<Directory> directories = new LinkedHashSet<>();
    private static LinkedHashSet<SearcherManager> searcherManagers = new LinkedHashSet<>();
    private static LinkedHashSet<ControlledRealTimeReopenThread> controlledRealTimeReopenThreads = new LinkedHashSet<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeAll()));
    }


    public static void closeOnExit(IndexReader indexReader) {
        indexReaders.add(indexReader);
    }


    public static void closeOnExit(IndexWriter indexWriter) {
        indexWriters.add(indexWriter);
    }


    public static void closeOnExit(Directory directory) {
        directories.add(directory);
    }


    public static void closeOnExit(SearcherManager searcherManager) {
        searcherManagers.add(searcherManager);
    }


    public static void closeOnExit(ControlledRealTimeReopenThread controlledRealTimeReopenThread) {
        controlledRealTimeReopenThreads.add(controlledRealTimeReopenThread);
    }


    private static void closeAll() {
        log.info("simsearch=>closing indexReaders...");
        indexReaders.forEach(e -> {
                    try {
                        e.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        indexReaders = null;
        log.info("simsearch=>closing indexWriters...");
        indexWriters.forEach(e -> {
                    try {
                        e.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        indexWriters = null;
        log.info("simsearch=>closing searcherManagers...");
        searcherManagers.forEach(e -> {
                    try {
                        e.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        searcherManagers = null;
        log.info("simsearch=>closing directories...");
        directories.forEach(e -> {
                    try {
                        e.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
        directories = null;
        log.info("simsearch=>closing controlledRealTimeReopenThreads...");
        controlledRealTimeReopenThreads.forEach(e -> {
                    e.close();
                }
        );
        controlledRealTimeReopenThreads = null;
    }

}
