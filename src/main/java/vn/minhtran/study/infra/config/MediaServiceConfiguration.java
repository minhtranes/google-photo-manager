package vn.minhtran.study.infra.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableScheduling
public class MediaServiceConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "media.download.executor")
	ThreadPoolTaskExecutor mediaDownloadExecutor() {
		return new ThreadPoolTaskExecutor();
	}

	@Bean
    protected MBeanExporter mbeanExporter() {
        MBeanExporter exporter = new MBeanExporter();
        Map<String,Object> beans = new HashMap<>();
        beans.put("org.springframework.boot:type=ThreadPoolTaskExecutor,name=MediaDownloadExecutor", mediaDownloadExecutor());
        exporter.setBeans(beans);
        return exporter;
    }
}
