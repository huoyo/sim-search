package cn.langpy.simsearch.config;


import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static java.io.File.separator;

public class IndexContextInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public static Logger log = Logger.getLogger(IndexContextInit.class.toString());

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        String indexPath = configurableApplicationContext.getEnvironment().getProperty("sim-search.dir");
        if (indexPath == null || indexPath.length() == 0) {
            indexPath = System.getProperty("user.dir")+separator+"indexs";
        }
        File file = new File(indexPath);
        if(!file.exists()) {
            log.info("indexPath is null,it will be created automatically :"+indexPath);
            file.mkdirs();
        }
    }
}
