package vn.minhtran.study.controller;

import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import vn.minhtran.study.infra.config.ObjectStorageProperties;
import vn.minhtran.study.model.AlbumInfo;
import vn.minhtran.study.service.AlbumService;
import vn.minhtran.study.service.impl.AlbumStatus;

@Component
public class MediaDownloadCompletionScheduler {

	private static Logger LOGGER = LoggerFactory
	        .getLogger(MediaDownloadCompletionScheduler.class);

	@Autowired
	private AlbumService albumService;

	@Value("${media.completion.cleanup.scheduler.enabled}")
	private boolean enabled;

	@PostConstruct
	void init() {
		LOGGER.info("Cleanup scheduler was set enabled = {}", enabled);
	}

	@Autowired
	private MinioClient minioClient;

	@Scheduled(cron = "${media.completion.cleanup.scheduler.cron}")
	public void check() {
		if (!enabled) {
			LOGGER.debug("Cleanup scheduler was disabled!");
			return;
		}
		ArrayNode downloadingAlbums = albumService
		        .listAlbum(AlbumStatus.DOWNLOADING);
		downloadingAlbums.forEach(album -> {
			checkAndMarkComplete(album);
		});
	}

	@Autowired
	private ObjectStorageProperties osProperties;

	private void checkAndMarkComplete(JsonNode album) {
		String id = album.findValue(AlbumInfo.FIELD_ALBUM_ID).textValue();
		int totalMediaCount = album
		        .findValue(AlbumInfo.FIELD_ALBUM_TOTAL_MEDIA_COUNT).intValue();

		Iterator<Result<Item>> it = minioClient
		        .listObjects(ListObjectsArgs.builder()
		                .bucket(osProperties.getBucket()).prefix(id).build())
		        .iterator();
		int downloadedMediaCount = 0;
		while (it.hasNext()) {
			it.next();
			downloadedMediaCount++;
		}
		if (totalMediaCount <= downloadedMediaCount) {
			LOGGER.info("Album [{}] was completely downloaded. Size [{}]", id,
			        downloadedMediaCount);
			albumService.downloadComplete(id);
		}
	}
}
