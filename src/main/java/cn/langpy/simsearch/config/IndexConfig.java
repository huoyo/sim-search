package cn.langpy.simsearch.config;

import cn.langpy.simsearch.util.IndexManager;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static java.io.File.separator;


@Configuration
@EnableAsync
public class IndexConfig {
    @Autowired
    private SimSearchConfig searchConfig;
    public static Logger log = Logger.getLogger(IndexConfig.class.toString());

    private static final String defaultIndexDirName = "indexs";
    @Bean
    public Directory directory() throws IOException {

        Directory directory = null;
        if ("memory".equals(searchConfig.getSaver())) {
            directory = new ByteBuffersDirectory();
        } else if ("memory-fs".equals(searchConfig.getSaver())) {
            Path path = Paths.get(checkDir());
            directory = MMapDirectory.open(path);
        } else if ("base-fs".equals(searchConfig.getSaver())){
            Path path = Paths.get(checkDir());
            directory = FSDirectory.open(path);
        } else if ("nio-fs".equals(searchConfig.getSaver())){
            Path path = Paths.get(checkDir());
            directory = NIOFSDirectory.open(path);
        }else {
            throw new RuntimeException("error `sim-search.saver`,please choice in [memory,memory-fs,base-fs,nio-fs]");
        }
        IndexManager.closeOnExit(directory);
        return directory;
    }

    public String checkDir() {
        String indexDir = searchConfig.getDir();
        if (indexDir == null || indexDir.length() == 0) {
            indexDir = System.getProperty("user.dir") + separator + defaultIndexDirName;
        }
        File file = new File(searchConfig.getDir());
        if (!file.exists()) {
            log.info("indexPath is null,it will be created automatically :" + searchConfig.getDir());
            file.mkdirs();
        }
        return indexDir;
    }

    @Bean("indexWriter")
    public IndexWriter indexWriter(Directory directory) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        if (searchConfig.getIndexInit()) {
            indexWriter.deleteAll();
            indexWriter.commit();
        }
        IndexManager.closeOnExit(indexWriter);
        return indexWriter;
    }

    @Bean("searcherManager")
    public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(indexWriter, searcherManager, 5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        cRTReopenThead.setName("Thread-update IndexReader");
        cRTReopenThead.start();
        IndexManager.closeOnExit(searcherManager);
        IndexManager.closeOnExit(cRTReopenThead);
        return searcherManager;
    }

    @Bean("indexExecutor")
    public ThreadPoolTaskExecutor taskExecutro() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(searchConfig.getThreadCoreSize());
        taskExecutor.setMaxPoolSize(searchConfig.getThreadMaxSize());
        taskExecutor.setQueueCapacity(searchConfig.getThreadQueueSize());
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("indexExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        return taskExecutor;
    }

}
