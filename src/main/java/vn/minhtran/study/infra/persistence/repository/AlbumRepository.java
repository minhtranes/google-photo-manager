package vn.minhtran.study.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {

}
