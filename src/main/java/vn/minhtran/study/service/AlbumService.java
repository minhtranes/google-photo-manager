package vn.minhtran.study.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface AlbumService {

	JsonNode list();

	JsonNode albumContent(String albumId) throws Exception;
}
