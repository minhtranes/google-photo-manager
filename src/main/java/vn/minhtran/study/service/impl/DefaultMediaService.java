package vn.minhtran.study.service.impl;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import vn.minhtran.study.infra.config.ObjectStorageProperties;
import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.infra.persistence.storage.MediaStorage;
import vn.minhtran.study.service.MediaService;

@Service
public class DefaultMediaService extends AbstractGooglePhoto
        implements MediaService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private ObjectStorageProperties osProperties;


	private static final Logger LOGGER = LoggerFactory
	        .getLogger(DefaultMediaService.class);

	@PostConstruct
	void init() {
		final String bucket = osProperties.getBucket();
		mediaStorage.makeBucket(bucket);
	}

	@Override
	public void downloadPhoto(String baseUrl, String albumName,
	        String albumTitle, String width, String height, String filename) {

		restTemplate.execute(baseUrl, HttpMethod.GET, null, res -> {
			String file = String.join("/", albumName, filename);
			try (InputStream is = res.getBody()) {
				try {
					mediaStorage.putObject(file, is);
				} catch (Exception e) {
					LOGGER.error("Failed to upload the image to object storage",
					        e);
				}
			}
			return file;
		});
	}

	@Autowired
	private MediaStorage mediaStorage;

	@Override
	protected CrudRepository<AlbumEntity, String> getRepository() {
		return null;
	}

}
