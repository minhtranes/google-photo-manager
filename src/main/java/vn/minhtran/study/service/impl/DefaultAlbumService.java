package vn.minhtran.study.service.impl;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.minhtran.study.service.AlbumService;

@Service
public class DefaultAlbumService extends AbstractGooglePhoto
		implements
			AlbumService {

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public JsonNode list() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken());
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(headers);
		ResponseEntity<JsonNode> exchange = restTemplate.exchange(
				"https://photoslibrary.googleapis.com/v1/albums",
				HttpMethod.GET, entity, JsonNode.class);
		if (exchange.getStatusCode() == HttpStatus.OK) {
			JsonNode body = exchange.getBody();
			return body;
		}

		return null;
	}

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public JsonNode albumContent(String albumId) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken());
		JsonNode bo = mapper.readTree("{\"pageSize\": \"100\",\"albumId\":\"" + albumId + "\"}");

		HttpEntity<String> entity = new HttpEntity<>(bo.toString(), headers);
		ResponseEntity<JsonNode> exchange = restTemplate.exchange(
				"https://photoslibrary.googleapis.com/v1/mediaItems:search",
				HttpMethod.POST, entity, JsonNode.class);
		if (exchange.getStatusCode() == HttpStatus.OK) {
			JsonNode body = exchange.getBody();
			return body;
		}

		return null;
	}

}
