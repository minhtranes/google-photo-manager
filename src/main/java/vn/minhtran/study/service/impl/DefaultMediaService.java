package vn.minhtran.study.service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteArgs;
import io.minio.PutObjectArgs;
import vn.minhtran.study.infra.config.LocalStorageProperties;
import vn.minhtran.study.infra.config.ObjectStorageProperties;
import vn.minhtran.study.infra.persistence.entity.AlbumEntity;
import vn.minhtran.study.service.MediaService;

@Service
public class DefaultMediaService extends AbstractGooglePhoto
        implements MediaService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private LocalStorageProperties storageProperties;

	@Autowired
	private ObjectStorageProperties osProperties;

	@Autowired
	private MinioClient minioClient;

	private static final Logger LOGGER = LoggerFactory
	        .getLogger(DefaultMediaService.class);

	@PostConstruct
	void init() {

		final String bucket = osProperties.getBucket();
		try {
			boolean bucketExists = minioClient.bucketExists(
			        BucketExistsArgs.builder().bucket(bucket).build());
			if (!bucketExists) {
				minioClient.makeBucket(
				        MakeBucketArgs.builder().bucket(bucket).build());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to create bucket [{}]", bucket, e);
		}
	}

	@Override
	public void downloadPhoto(String baseUrl, String albumName,
	        String albumTitle, String width, String height, String filename) {

		restTemplate.execute(baseUrl, HttpMethod.GET, null, res -> {
			Path path = Paths.get(storageProperties.getDirectory(), albumName,
			        filename);
			File ret = path.toFile();
			if (!ret.getParentFile().exists()) {
				ret.getParentFile().mkdirs();
			}
			if (!ret.exists()) {
				ret.createNewFile();
			}

			try (InputStream is = res.getBody()) {
				try {
					minioClient.putObject(PutObjectArgs.builder()
					        .bucket(osProperties.getBucket())
					        .object(String.join("/", albumName, filename))
					        .stream(is, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE)
					        .contentType("image/jpg").build());
				} catch (Exception e) {
					LOGGER.error("Failed to upload the image to object storage",
					        e);
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
