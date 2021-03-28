package vn.minhtran.study.infra.persistence.storage.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
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
	private ObjectStorageProperties properties;

	@Override
	public int countObject(String prefix) {

		int count = 0;

		try {
			Iterator<Result<Item>> it = client.listObjects(
			        ListObjectsArgs.builder().bucket(properties.getBucket())
			                .prefix(prefix).build())
			        .iterator();

			while (it.hasNext()) {
				it.next();
				count++;
			}
		} catch (Exception e) {
			LOGGER.error(
			        "Failed to count object from bucket [{}] with prefix [{}]",
			        properties.getBucket(), prefix);
		}

		return count;
	}

}
