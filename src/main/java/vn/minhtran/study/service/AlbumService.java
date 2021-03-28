package vn.minhtran.study.service;

import com.fasterxml.jackson.databind.node.ArrayNode;

import vn.minhtran.study.service.impl.AlbumStatus;

public interface AlbumService {

	ArrayNode list();

	ArrayNode listAlbumMedia(String albumId) throws Exception;

	AlbumStatus albumLocalStatus(String albumId);

	void addAlbum(String albumId, String albumTitle, int size);

	int getAlbumSize(String albumId);

	void downloadComplete(String albumId);

	ArrayNode listAlbum(AlbumStatus... statuses);

}
