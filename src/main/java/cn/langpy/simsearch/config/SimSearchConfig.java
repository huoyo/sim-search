package cn.langpy.simsearch.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * zhangchang
 */
@Component
@ConfigurationProperties(prefix = "sim-search")
public class SimSearchConfig {
    private String dir;
    private String saver = "memory";
    @Deprecated
    private Integer sizeCore = 5;
    private Integer threadCoreSize = 5;
    @Deprecated
    private Integer sizeMax = 200;
    private Integer threadMaxSize = 200;
    @Deprecated
    private Integer sizeQueue = 20000;
    private Integer threadQueueSize = 200000;
    private Integer resultSize = 50;
    @Deprecated
    private Boolean indexInit = false;

    public Integer getResultSize() {
        return resultSize;
    }

    public void setResultSize(Integer resultSize) {
        this.resultSize = resultSize;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getSaver() {
        return saver;
    }

    public void setSaver(String saver) {
        this.saver = saver;
    }

    public Integer getSizeCore() {
        return sizeCore;
    }

    public void setSizeCore(Integer sizeCore) {
        this.sizeCore = sizeCore;
    }

    public Integer getThreadCoreSize() {
        return threadCoreSize;
    }

    public void setThreadCoreSize(Integer threadCoreSize) {
        this.threadCoreSize = threadCoreSize;
    }

    public Integer getSizeMax() {
        return sizeMax;
    }

    public void setSizeMax(Integer sizeMax) {
        this.sizeMax = sizeMax;
    }

    public Integer getThreadMaxSize() {
        return threadMaxSize;
    }

    public void setThreadMaxSize(Integer threadMaxSize) {
        this.threadMaxSize = threadMaxSize;
    }


    public Integer getSizeQueue() {
        return sizeQueue;
    }

    public void setSizeQueue(Integer sizeQueue) {
        this.sizeQueue = sizeQueue;
    }

    public Integer getThreadQueueSize() {
        return threadQueueSize;
    }

    public void setThreadQueueSize(Integer threadQueueSize) {
        this.threadQueueSize = threadQueueSize;
    }

    public Boolean getIndexInit() {
        return indexInit;
    }

    public void setIndexInit(Boolean indexInit) {
        this.indexInit = indexInit;
    }
}
