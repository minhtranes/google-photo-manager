package vn.minhtran.study.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.minhtran.study.infra.persistence.storage.MediaStorage;
import vn.minhtran.study.model.AlbumInfo;
import vn.minhtran.study.service.AlbumService;
import vn.minhtran.study.service.MediaService;
import vn.minhtran.study.service.impl.AlbumStatus;

@RestController
@RequestMapping("/albums")
public class AlbumController {

	private static Logger LOGGER = LoggerFactory
	        .getLogger(AlbumController.class);

	@Autowired
	private AlbumService albumService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private ThreadPoolTaskExecutor mediaDownloadExecutor;

	@GetMapping("/download")
	public JsonNode downloadAlbums(Authentication authentication,
	        @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
	        @RequestParam(name = "forced", required = false, defaultValue = "false") boolean forced)
	        throws IOException {
		if (limit == 0) {
			return null;
		}
		ArrayNode albums = albumService.list();
		if (albums.isArray()) {
			int count = 0;
			for (JsonNode album : albums) {
				downloadAlbum((ObjectNode) album, forced);
				count++;
				if (limit > 0 && count >= limit) {
					break;
				}
			}
		}

		return albums;
	}

	@GetMapping("/check-and-redownload/{albumIds}")
	public JsonNode checkAndRedownload(
	        @PathVariable(name = "albumId", required = false) String albumIds) {

		ArrayNode downloadingAlbums = null;
		if (albumIds != null) {
			String[] albumIdss = albumIds.split(",");
			downloadingAlbums = new ObjectMapper().createArrayNode();

			for (String albumId : albumIdss) {
				ObjectNode album = albumService.getAlbum(albumId);
				downloadingAlbums.add(album);
			}
		} else {
			downloadingAlbums = albumService.listAlbum(AlbumStatus.DOWNLOADING);
		}
		downloadingAlbums.forEach(album -> {
			checkAndRedownload(album);
		});

		return downloadingAlbums;
	}

	private void checkAndRedownload(JsonNode album) {
		String albumId = album.findValue(AlbumInfo.FIELD_ALBUM_ID).textValue();
		String albumTitle = album.findValue(AlbumInfo.FIELD_ALBUM_TITLE)
		        .textValue();
		int totalMediaCount = album
		        .findValue(AlbumInfo.FIELD_ALBUM_TOTAL_MEDIA_COUNT).intValue();
		int downloadedMediaCount = mediaStorage.countObject(albumId);
		if (downloadedMediaCount < totalMediaCount) {
			LOGGER.info(
			        "Album [{}] has downloaded {}/{} media. Re-download it.",
			        albumId, downloadedMediaCount, totalMediaCount);
			actuallyDownloadAlbum((ObjectNode) album, albumId, albumTitle);
		} else {
			((ObjectNode) album).put("ignoredReason",
			        "Download media equals or greater than total media");
		}
	}

	@Autowired
	private MediaStorage mediaStorage;

	@GetMapping("/list")
	public JsonNode listAlbums(Authentication authentication)
	        throws IOException {
		LOGGER.info("Listing....");
		return albumService.list();
	}

	private void downloadAlbum(ObjectNode album, boolean forced) {

		String albumId = album.findValue("id").textValue();
		String albumTitle = album.findValue("title").textValue();

		if (shouldDownloadAlbum(albumId, albumTitle, forced)) {

			actuallyDownloadAlbum(album, albumId, albumTitle);
		}
	}

	private void actuallyDownloadAlbum(ObjectNode album, String albumId,
	        String albumTitle) {
		LOGGER.info("Reading album {}...", albumTitle);
		try {
			ArrayNode albumMedias = albumService.listAlbumMedia(albumId);
			if (albumMedias.isArray()) {
				int size = albumMedias.size();
				album.put("totalMedia", size);
				LOGGER.info("Album [{}] has {} media", albumId, size);
				albumService.addAlbum(albumId, albumTitle, size);
				for (JsonNode media : albumMedias) {
					String filename = media.findValue("filename").textValue();
					JsonNode mediaMetadata = media.findParent("mediaMetadata");
					String width = mediaMetadata.findValue("width").textValue();
					String height = mediaMetadata.findValue("height")
					        .textValue();
					final String baseUrl = String.format("%s=w%s-h%s",
					        media.findValue("baseUrl").textValue(), width,
					        height);
					mediaDownloadExecutor.execute(() -> {
						LOGGER.info("Download file [{}]...", filename);
						mediaService.downloadPhoto(baseUrl, albumId, albumTitle,
						        width, height, filename);
					});
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error when process album [{}]", albumTitle, e);
		}
	}

	private boolean shouldDownloadAlbum(String albumId, String albumTitle,
	        boolean forced) {
		if (forced) {
			return true;
		}
		AlbumStatus status = albumService.getAlbumStatus(albumId);
		return status == null || status == AlbumStatus.DOWNLOADING ? true
		        : false;
	}

}
