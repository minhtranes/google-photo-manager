package vn.minhtran.study.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;

@Configuration
@Profile("minio")
public class MinioConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "storage.os")
	ObjectStorageProperties osProperties() {
		return new ObjectStorageProperties();
	}

	@Bean
	MinioClient minioClient() {
		ObjectStorageProperties credential = osProperties();
		return MinioClient
				.builder()
				.endpoint(credential.getEndpoint())
		        .credentials(credential.getAccesskey(),
		                credential.getSecretkey())
		        .build();
	}

}
