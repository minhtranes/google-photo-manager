package vn.minhtran.study.service;

import com.fasterxml.jackson.databind.JsonNode;

import vn.minhtran.study.service.impl.AlbumStatus;

public interface AlbumService {

	JsonNode list();

	JsonNode albumContent(String albumId) throws Exception;

	AlbumStatus albumLocalStatus(String albumId);

	void addAlbum(String albumId, String albumTitle);
}
