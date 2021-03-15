package vn.minhtran.study.service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.waiters.WaiterParameters;

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
	
	@Autowired
	private AmazonS3Client s3Client;
	
	@PostConstruct
	void init() {
		s3Client
		  .waiters()
		  .bucketExists()
		  .run(
		    new WaiterParameters<>(
		      new HeadBucketRequest("my-awesome-bucket")
		    )
		  );
	}

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
//					try (InputStream is = clientHttpResponse.getBody()) {
//						try (FileOutputStream os = new FileOutputStream(ret)) {
//							StreamUtils.copy(is, os);
//						}
//					}
					
					try (InputStream is = clientHttpResponse.getBody()) {
						ObjectMetadata metadata=new ObjectMetadata();
						s3Client.putObject("my-awesome-bucket", filename, is, metadata);
					}
					return ret;
				});
	}

	@Override
	protected CrudRepository<AlbumEntity, String> getRepository() {
		return null;
	}

}
