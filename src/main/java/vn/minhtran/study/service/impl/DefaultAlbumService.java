package vn.minhtran.study.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.infra.persistence.repository.AlbumRepository;
import vn.minhtran.study.model.AlbumInfo;
//import vn.minhtran.study.infra.persistence.repository.AlbumRepository;
import vn.minhtran.study.service.AlbumService;

@Service
public class DefaultAlbumService extends AbstractGooglePhoto
        implements AlbumService {

	private static final String FIELD_ALBUMS = "albums";
	private static final String FIELD_MEDIA_ITEMS = "mediaItems";
	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public ArrayNode list() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken());

		ArrayNode ret = mapper.createArrayNode();

		UriComponentsBuilder builder = UriComponentsBuilder
		        .fromHttpUrl("https://photoslibrary.googleapis.com/v1/albums")
		        .queryParam("pageSize", 50);
		boolean hasNext = false;
		try {
			do {
				HttpEntity<Map<String, String>> entity = new HttpEntity<>(
				        headers);

				ResponseEntity<JsonNode> exchange = restTemplate.exchange(
				        builder.build().toUri(), HttpMethod.GET, entity,
				        JsonNode.class);
				if (exchange.getStatusCode() == HttpStatus.OK) {
					JsonNode body = exchange.getBody();
					String pageToken = null;
					if ((pageToken = hasNextPageToken(body)) != null) {
						mergeResult(ret, body, FIELD_ALBUMS);
						builder.replaceQueryParam("pageToken", pageToken);
						hasNext = true;
					} else {
						hasNext = false;
					}
				}
			} while (hasNext);
		} finally {
			LOGGER.info("Found {} albums from google photo", ret.size());
		}

		return ret;
	}

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(DefaultAlbumService.class);
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public ArrayNode listAlbum(AlbumStatus... statuses) {
		List<AlbumEntity> entities = albumRepository.findByStatus(statuses[0]);
		if (entities == null || entities.size() <= 0) {
			return null;
		}
		ArrayNode ret = mapper.createArrayNode();
		entities.forEach(e -> {
			ret.add(fromEntity(e));
		});
		return ret;
	}

	@Override
	public ObjectNode getAlbum(String albumId) {
		Optional<AlbumEntity> eo = albumRepository.findById(albumId);
		if (eo.isPresent()) {
			return fromEntity(eo.get());
		}
		return null;
	}

	private ObjectNode fromEntity(AlbumEntity e) {
		ObjectNode o = mapper.createObjectNode();
		{
			o.put(AlbumInfo.FIELD_ALBUM_ID, e.getAlbumId());
			o.put(AlbumInfo.FIELD_ALBUM_TITLE, e.getTitle());
			o.put(AlbumInfo.FIELD_ALBUM_TOTAL_MEDIA_COUNT,
			        e.getNumOfImage().intValue());
			o.put(AlbumInfo.FIELD_ALBUM_STATUS, e.getStatus());
		}
		return o;
	}

	@Override
	public ArrayNode listAlbumMedia(String albumId) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken());

		String bodyWithoutPage = mapper.readTree(
		        "{\"pageSize\": \"25\",\"albumId\":\"" + albumId + "\"}")
		        .toString();
		final String searchURL = "https://photoslibrary.googleapis.com/v1/mediaItems:search";

		String bodyWithPage = null;
		ArrayNode ret = mapper.createArrayNode();

		boolean hasNext = false;
		String nextPageToken = null;
		try {
			do {
				HttpEntity<String> entity = new HttpEntity<>(
				        hasNext ? bodyWithPage : bodyWithoutPage, headers);
				ResponseEntity<JsonNode> exchange = restTemplate.exchange(
				        searchURL, HttpMethod.POST, entity, JsonNode.class);
				if (exchange.getStatusCode() == HttpStatus.OK) {
					ObjectNode body = (ObjectNode) exchange.getBody();
					if ((nextPageToken = hasNextPageToken(body)) != null) {
						mergeResult(ret, body, FIELD_MEDIA_ITEMS);
						hasNext = true;
						bodyWithPage = mapper
						        .readTree("{\"pageSize\": \"25\",\"albumId\":\""
						                + albumId + "\",\"pageToken\":\""
						                + nextPageToken + "\"}")
						        .toString();
					} else {
						hasNext = false;
						bodyWithPage = null;
					}
				}
			} while (hasNext);
		} finally {
			LOGGER.info("Found {} media from album [{}]", ret.size(), albumId);
		}

		return ret;
	}

	private void mergeResult(ArrayNode ret, JsonNode body, String arrayField) {
		try {
			JsonNode addedMediaItems = body.findValue(arrayField);
			if (addedMediaItems.isArray()) {
				ret.addAll((ArrayNode) addedMediaItems);
			}
		} catch (Exception e) {
			LOGGER.error(
			        "Failed to merge result from a page to the total result",
			        e);
		}
	}

	private String hasNextPageToken(JsonNode body) {
		if (body == null) {
			return null;
		}
		JsonNode nextPageNode = body.findValue("nextPageToken");
		return nextPageNode == null ? null : nextPageNode.textValue();
	}

	@PostConstruct
	void init() {
		restore();
	}

	@Autowired
	private AlbumRepository albumRepository;

	@Override
	protected CrudRepository<AlbumEntity, String> getRepository() {
		return albumRepository;
	}

	@Override
	public AlbumStatus getAlbumStatus(String albumId) {
		AlbumEntity albumEntity = get(albumId);
		return albumEntity == null ? null
		        : AlbumStatus.valueOf(albumEntity.getStatus());
	}

	@Override
	public void addAlbum(String albumId, String albumTitle, int size) {
		AlbumEntity en = new AlbumEntity();
		en.setAlbumId(albumId);
		en.setTitle(albumTitle);
		en.setStatus(AlbumStatus.DOWNLOADING.name());
		en.setNumOfImage(size);
		put(en);
	}

	@Override
	public int getAlbumSize(String albumId) {
		AlbumEntity albumEntity = get(albumId);
		return albumEntity == null ? 0 : albumEntity.getNumOfImage().intValue();
	}

	@Override
	public void downloadComplete(String albumId) {
		AlbumEntity albumEntity = get(albumId);
		if (albumEntity != null) {
			albumEntity.setStatus(AlbumStatus.DOWNLOAD_COMPLETED.name());
			put(albumEntity);
		}
	}
}
