package vn.minhtran.study.infra.persistence.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;

@EnableScan
public interface AlbumRepository extends CrudRepository<AlbumEntity, String> {

}
