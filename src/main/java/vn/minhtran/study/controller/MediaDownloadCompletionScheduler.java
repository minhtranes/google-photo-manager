package vn.minhtran.study.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import vn.minhtran.study.infra.persistence.storage.MediaStorage;
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
	private MediaStorage storage;

	private void checkAndMarkComplete(JsonNode album) {
		String id = album.findValue(AlbumInfo.FIELD_ALBUM_ID).textValue();
		int totalMediaCount = album
		        .findValue(AlbumInfo.FIELD_ALBUM_TOTAL_MEDIA_COUNT).intValue();

		int downloadedMediaCount = storage.countObject(id);
		if (totalMediaCount <= downloadedMediaCount) {
			LOGGER.info("Album [{}] was completely downloaded. Size [{}]", id,
			        downloadedMediaCount);
			albumService.downloadComplete(id);
		}
	}
}
