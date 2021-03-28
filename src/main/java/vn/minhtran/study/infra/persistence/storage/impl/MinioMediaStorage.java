package vn.minhtran.study.infra.persistence.storage.impl;

import java.io.InputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteArgs;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import vn.minhtran.study.infra.config.ObjectStorageProperties;
import vn.minhtran.study.infra.persistence.storage.MediaStorage;

@Component
public class MinioMediaStorage implements MediaStorage {
	private static final Logger LOGGER = LoggerFactory
	        .getLogger(MinioMediaStorage.class);

	@Autowired
	private MinioClient client;

	@Autowired
	private ObjectStorageProperties osProperties;

	@Override
	public int countObject(String prefix) {

		int count = 0;

		try {
			Iterator<Result<Item>> it = client.listObjects(
			        ListObjectsArgs.builder().bucket(osProperties.getBucket())
			                .prefix(prefix).build())
			        .iterator();

			while (it.hasNext()) {
				it.next();
				count++;
			}
		} catch (Exception e) {
			LOGGER.error(
			        "Failed to count object from bucket [{}] with prefix [{}]",
			        osProperties.getBucket(), prefix);
		}

		return count;
	}

	@Override
	public void putObject(String key, InputStream is) throws Exception {
		client.putObject(PutObjectArgs.builder().bucket(osProperties.getBucket())
		        .object(key).stream(is, -1, ObjectWriteArgs.MIN_MULTIPART_SIZE)
		        .contentType("image/jpg").build());
	}

	@Override
	public void makeBucket(String bucket) {
		try {
			boolean bucketExists = client.bucketExists(
			        BucketExistsArgs.builder().bucket(bucket).build());
			if (!bucketExists) {
				client.makeBucket(
				        MakeBucketArgs.builder().bucket(bucket).build());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to create bucket [{}]", bucket, e);
		}
	}

}
