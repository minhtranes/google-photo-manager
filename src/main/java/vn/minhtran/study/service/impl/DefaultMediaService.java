package vn.minhtran.study.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import vn.minhtran.study.infra.config.LocalStorageProperties;
import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.service.MediaService;

@Service
public class DefaultMediaService extends AbstractGooglePhoto
		implements
			MediaService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private LocalStorageProperties storageProperties;

	@Override
	public void downloadPhoto(String baseUrl, String albumName, String width,
			String height, String filename) {

		restTemplate.execute(baseUrl, HttpMethod.GET, null,
				clientHttpResponse -> {
					Path path = Paths.get(storageProperties.getDirectory(),
							albumName, filename);
					File ret = path.toFile();
					if (!ret.getParentFile().exists()) {
						ret.getParentFile().mkdirs();
					}
					if (!ret.exists()) {
						ret.createNewFile();
					}
					try (InputStream is = clientHttpResponse.getBody()) {
						try (FileOutputStream os = new FileOutputStream(ret)) {
							StreamUtils.copy(is, os);
						}
					}
					return ret;
				});
	}

	@Override
	protected CrudRepository<AlbumEntity, String> getRepository() {
		return null;
	}

}
