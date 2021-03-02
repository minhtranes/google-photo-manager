package vn.minhtran.study.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import vn.minhtran.study.infra.config.LocalStorageProperties;
import vn.minhtran.study.service.AlbumService;

@Component
public class MediaDownloadCompletionScheduler {

	private static Logger LOGGER = LoggerFactory
			.getLogger(MediaDownloadCompletionScheduler.class);

	@Autowired
	private AlbumService albumService;

	@Autowired
	private LocalStorageProperties storageProperties;

	@Scheduled(fixedDelayString = "5000")
	public void check() {
		File directory = new File(storageProperties.getDirectory());
		File[] listFiles = directory.listFiles();
		File archiveDir = new File(storageProperties.getArchiveDirectory());
		if (!archiveDir.exists()) {
			archiveDir.mkdirs();
		}
		for (File dir : listFiles) {
			if (dir.isDirectory() && !archiveDir.equals(dir)) {
				File[] images = dir.listFiles();
				String albumId = dir.getName();
				int albumSize = albumService.getAlbumSize(albumId);
				if (images.length == albumSize) {
					LOGGER.info("Album {} download completed", albumId);
					albumService.downloadComplete(albumId);
					try {
						Files.move(dir.toPath(),
								archiveDir.toPath().resolve(albumId),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						LOGGER.error("Failed to move dir from [{}] to [{}]",
								dir.toString(), archiveDir.toString(), e);
					}
				}
			}
		}
	}
}
