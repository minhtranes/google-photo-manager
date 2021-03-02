package vn.minhtran.study.infra.persistence.entity;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import vn.minhtran.study.infra.cache.KeyEntity;

@Entity
@Table(name = "albums")
@Access(AccessType.PROPERTY)
public class AlbumEntity implements Serializable, KeyEntity<String> {

	private static final long serialVersionUID = 440751448351914335L;

	private String albumId;
	private String title;
	private String status;
	private Integer numOfImage;

	public Integer getNumOfImage() {
		return numOfImage;
	}
	public void setNumOfImage(Integer numOfImage) {
		this.numOfImage = numOfImage;
	}
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	@Override
	public String getKey() {
		return albumId;
	}
}
