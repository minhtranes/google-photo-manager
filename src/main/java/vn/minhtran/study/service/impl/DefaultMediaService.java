package vn.minhtran.study.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
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
					StreamUtils.copy(clientHttpResponse.getBody(),
							new FileOutputStream(ret));
					return ret;
				});
	}

	@Override
	protected JpaRepository<AlbumEntity, Long> getRepository() {
		return null;
	}

}
