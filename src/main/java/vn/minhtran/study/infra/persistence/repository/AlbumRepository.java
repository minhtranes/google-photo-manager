package vn.minhtran.study.infra.persistence.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.service.impl.AlbumStatus;

@EnableScan
public interface AlbumRepository extends CrudRepository<AlbumEntity, String> {

	List<AlbumEntity> findByStatus(AlbumStatus[] statuses);
}
