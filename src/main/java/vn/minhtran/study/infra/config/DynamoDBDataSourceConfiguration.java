/*
 * Class: DynamoDataSourceConfiguration
 *
 * Created on Nov 23, 2020
 *
 * (c) Copyright Swiss Post Solutions Ltd, unpublished work
 * All use, disclosure, and/or reproduction of this material is prohibited
 * unless authorized in writing.  All Rights Reserved.
 * Rights in this program belong to:
 * Swiss Post Solution.
 * Floor 4-5-8, ICT Tower, Quang Trung Software City
 */
package vn.minhtran.study.infra.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.infra.persistence.repository.AlbumRepository;

@Configuration
@Profile("dynamodb")
@EnableJpaRepositories(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = AlbumRepository.class))
@EnableDynamoDBRepositories(basePackageClasses = { AlbumRepository.class })
public class DynamoDBDataSourceConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "amazon.aws")
	ObjectStorageProperties awsProperties() {
		return new ObjectStorageProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "amazon.dynamodb")
	DynamoDBProperties dynamoDBProperties() {
		return new DynamoDBProperties();
	}

	public AWSCredentialsProvider amazonAWSCredentialsProvider() {
		return new AWSStaticCredentialsProvider(amazonAWSCredentials());
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
		        .withCredentials(amazonAWSCredentialsProvider())
		        .withRegion(Regions.AP_NORTHEAST_1).build();
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(awsProperties().getAccesskey(),
		        awsProperties().getSecretkey());
	}

	@Bean
	Object initDynamo(
	        @Value("${spring.datasource.continue-on-error:false}") boolean continueOnError) {

		try {
			final DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(
			        amazonDynamoDB());
			final CreateTableRequest tableRequest = dynamoDBMapper
			        .generateCreateTableRequest(AlbumEntity.class);
			tableRequest.setProvisionedThroughput(
			        new ProvisionedThroughput(1L, 1L));
			amazonDynamoDB().createTable(tableRequest);
		} catch (Exception e) {
			if (!continueOnError) {
				throw e;
			}
		}
		return new Object();
	}
}
