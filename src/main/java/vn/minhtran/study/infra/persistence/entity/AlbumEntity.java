package vn.minhtran.study.infra.persistence.entity;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import vn.minhtran.study.infra.cache.KeyEntity;

@Entity
@Table(name = "albums")
@Access(AccessType.PROPERTY)
public class AlbumEntity implements Serializable,KeyEntity<Long> {

	private static final long serialVersionUID = 440751448351914335L;

	private Long id;
	private String albumId;
	private String title;
	private String status;
	
	@Id
    @Column(name = "id",columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAlbumId() {
		return albumId;
	}
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Transient
	@Override
	public Long getKey() {
		return id;
	}
}
