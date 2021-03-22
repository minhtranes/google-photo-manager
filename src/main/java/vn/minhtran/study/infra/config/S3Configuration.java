package vn.minhtran.study.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
@Profile("s3")
public class S3Configuration {

	@Bean
	EndpointConfiguration s3EndpointConfiguration(@Value("${amazon.s3.endpoint}") String serviceEndpoint,
			@Value("${amazon.s3.region}") String signingRegion) {
		return new EndpointConfiguration(serviceEndpoint, signingRegion);
	}

	@Bean
	public AWSCredentials amazonS3Credentials() {
		return new BasicAWSCredentials(awsS3Credential().getAccesskey(), awsS3Credential().getSecretkey());
	}

	@Bean
	@ConfigurationProperties(prefix = "amazon.s3")
	ObjectStorageProperties awsS3Credential() {
		return new ObjectStorageProperties();
	}

	@Primary
	@Bean(destroyMethod = "shutdown")
	public AmazonS3 amazonS3(EndpointConfiguration s3EndpointConfiguration,
			AWSCredentialsProvider amazonAWSCredentialsProvider) {
		return AmazonS3ClientBuilder.standard()
				
				.withEndpointConfiguration(s3EndpointConfiguration)
				.withCredentials(amazonAWSCredentialsProvider).build();
	}
}
