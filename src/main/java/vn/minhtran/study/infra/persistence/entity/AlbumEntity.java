package vn.minhtran.study.infra.persistence.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;

import vn.minhtran.study.infra.cache.KeyEntity;

@Entity
@DynamoDBTable(tableName = "google_photo_albums")
@AccessType(Type.PROPERTY)
public class AlbumEntity implements Serializable, KeyEntity<String> {

	private static final long serialVersionUID = 2773857062404197988L;
	private String albumId;
	private String title;
	private String status;
	private Integer numOfImage;

	@DynamoDBAttribute
	@DynamoDBTyped(DynamoDBAttributeType.N)
	public Integer getNumOfImage() {
		return numOfImage;
	}
	public void setNumOfImage(Integer numOfImage) {
		this.numOfImage = numOfImage;
	}

	@Id
	@DynamoDBHashKey
	@DynamoDBTyped(DynamoDBAttributeType.S)
	public String getAlbumId() {
		return albumId;
	}
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	@DynamoDBAttribute
	@DynamoDBTyped(DynamoDBAttributeType.S)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@DynamoDBAttribute
	@DynamoDBTyped(DynamoDBAttributeType.S)
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
