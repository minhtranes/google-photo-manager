package vn.minhtran.study.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import vn.minhtran.study.infra.persistence.repository.AlbumRepository;

@Configuration
@ConditionalOnMissingBean(value = DynamoDBDataSourceConfiguration.class)
@EnableJpaRepositories(basePackageClasses = {AlbumRepository.class})
public class DefaultDataSourceConfiguration {

}
