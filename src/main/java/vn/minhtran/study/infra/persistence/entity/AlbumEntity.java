package vn.minhtran.study.infra.persistence.entity;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import vn.minhtran.study.infra.cache.KeyEntity;
import vn.minhtran.study.service.impl.AlbumStatus;

@Entity
@Table(name = "albums")
@Access(AccessType.PROPERTY)
public class AlbumEntity implements Serializable, KeyEntity<String> {

	private static final long serialVersionUID = 440751448351914335L;

	private String albumId;
	private String title;
	private AlbumStatus status;

	@Id
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
	public AlbumStatus getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = AlbumStatus.valueOf(status);
	}

	@Transient
	@Override
	public String getKey() {
		return albumId;
	}
}
