package cn.langpy.simsearch.config;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import static java.io.File.separator;


@Configuration
@EnableAsync
public class IndexConfig {
    String indexLocalDir=System.getProperty("user.dir")+separator+"indexs";

    @Value("${sim-search.dir:}")
    String indexDir;
    @Value("${sim-search.size.core:10}")
    Integer coreSize;
    @Value("${sim-search.size.max:200}")
    Integer maxSize;
    @Value("${sim-search.size.queue:20000}")
    Integer queueSize;
    @Value("${sim-search.index.init:false}")
    boolean indexInit;

    @Bean
    public Directory directory() throws IOException {
        if (StringUtils.isEmpty(indexDir)) {
            indexDir = indexLocalDir;
        }
        File file = new File(indexDir);
        return FSDirectory.open(file);
    }

    @Bean
    public IndexWriter indexWriter(Directory directory) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        if (indexInit) {
            indexWriter.deleteAll();
            indexWriter.commit();
        }
        return indexWriter;
    }

    @Bean
    public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, false, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(new TrackingIndexWriter(indexWriter), searcherManager, 5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        cRTReopenThead.setName("Thread-update IndexReader");
        cRTReopenThead.start();
        return searcherManager;
    }

    @Bean("indexExecutor")
    public Executor taskExecutro(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(coreSize);
        taskExecutor.setMaxPoolSize(maxSize);
        taskExecutor.setQueueCapacity(queueSize);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("indexExecutor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        return taskExecutor;
    }

}
