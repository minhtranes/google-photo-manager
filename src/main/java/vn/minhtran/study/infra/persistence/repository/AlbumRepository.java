package vn.minhtran.study.infra.persistence.repository;

import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;

@EnableScan
public interface AlbumRepository
		extends
			DynamoDBCrudRepository<AlbumEntity, String> {

}
