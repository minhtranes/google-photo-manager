package vn.minhtran.study.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import vn.minhtran.study.infra.persistence.repository.AlbumRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = { AlbumRepository.class })
public class DefaultDataSourceConfiguration {

}
