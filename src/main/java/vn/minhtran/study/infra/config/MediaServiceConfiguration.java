package vn.minhtran.study.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
