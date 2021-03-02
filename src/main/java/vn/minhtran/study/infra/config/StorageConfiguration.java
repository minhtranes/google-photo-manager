package vn.minhtran.study.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "storage")
	LocalStorageProperties localStorageProperties() {
		return new LocalStorageProperties();
	}
}
