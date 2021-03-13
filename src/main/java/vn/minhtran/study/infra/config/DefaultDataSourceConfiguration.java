package vn.minhtran.study.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "vn.minhtran.study.infra.persistence.h2")
public class DefaultDataSourceConfiguration {

}
