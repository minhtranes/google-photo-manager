package vn.minhtran.study.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnMissingBean(value = DynamoDBDataSourceConfiguration.class)
@EnableJpaRepositories(basePackages = "vn.minhtran.study.infra.persistence")
public class DefaultDataSourceConfiguration {

}
