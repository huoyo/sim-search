package cn.langpy.simsearch.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;

/**
 * @name：索引基础配置
 * @function：
 * @author：zhangchang
 * @date 2020/9/27 10:07
 */
@Configuration
@EnableAsync
public class IndexConfig {
    String indexLocalDir=System.getProperty("user.dir")+"/indexs";

    @Value("${sim-search.dir:}")
    String indexDir;
    @Value("${sim-search.size.core:10}")
    Integer coreSize;
    @Value("${sim-search.size.max:200}")
    Integer maxSize;
    @Value("${sim-search.size.queue:20000}")
    Integer queueSize;

    @Bean
    public Directory directory() throws IOException {
        if (StringUtils.isEmpty(indexDir)) {
            indexDir = indexLocalDir;
        }
        Path path = Paths.get(indexDir);
        File file = path.toFile();
        if(!file.exists()) {
            file.mkdirs();
        }
        return FSDirectory.open(file);
    }
    @Bean
    public IndexReader reader(Directory directory) {
        IndexReader reader = null;
        try {
            reader = DirectoryReader.open(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }
    @Bean
    public IndexSearcher searcher(IndexReader reader) {
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

    @Bean
    public IndexWriter indexWriter(Directory directory) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST,new StandardAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
//        indexWriter.deleteAll();
//        indexWriter.commit();
        return indexWriter;
    }

    @Bean
    public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
        SearcherManager searcherManager = new SearcherManager(indexWriter, false, new SearcherFactory());
        ControlledRealTimeReopenThread cRTReopenThead = new ControlledRealTimeReopenThread(new TrackingIndexWriter(indexWriter), searcherManager, 5.0, 0.025);
        cRTReopenThead.setDaemon(true);
        cRTReopenThead.setName("更新IndexReader线程");
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
