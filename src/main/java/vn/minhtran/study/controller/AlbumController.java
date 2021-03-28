package vn.minhtran.study.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.minhtran.study.service.AlbumService;
import vn.minhtran.study.service.MediaService;
import vn.minhtran.study.service.impl.AlbumStatus;

@RestController
@RequestMapping("albums")
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
		JsonNode albums = albumService.list();
		JsonNode albumsCon = albums.findValue("albums");
		if (albumsCon.isArray()) {
			int count = 0;
			for (JsonNode al : albumsCon) {
				downloadAlbum((ObjectNode) al, forced);
				count++;
				if (limit > 0 && count >= limit) {
					break;
				}
			}
		}

		return albums;
	}

	@GetMapping("/resume")
	public JsonNode resumeDownload() {
		ArrayNode downloadingAlbums = albumService
		        .listAlbum(AlbumStatus.DOWNLOADING);
		downloadingAlbums.forEach(album -> {
			downloadAlbum((ObjectNode) album, false);
		});

		return downloadingAlbums;
	}

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

			LOGGER.info("Reading album {}...", albumTitle);
			try {
				JsonNode albumContent = albumService.albumContent(albumId);
				JsonNode mediaItemsCon = albumContent.findValue("mediaItems");
				if (mediaItemsCon.isArray()) {
					int size = mediaItemsCon.size();
					album.put("totalMedia", size);
					LOGGER.info("Album [{}] has {} media", albumId, size);
					albumService.addAlbum(albumId, albumTitle, size);
					for (JsonNode mcj : mediaItemsCon) {
						String filename = mcj.findValue("filename").textValue();
						JsonNode mediaMetadata = mcj
						        .findParent("mediaMetadata");
						String width = mediaMetadata.findValue("width")
						        .textValue();
						String height = mediaMetadata.findValue("height")
						        .textValue();
						final String baseUrl = String.format("%s=w%s-h%s",
						        mcj.findValue("baseUrl").textValue(), width,
						        height);
						mediaDownloadExecutor.execute(() -> {
							LOGGER.info("Download file [{}]...", filename);
							mediaService.downloadPhoto(baseUrl, albumId,
							        albumTitle, width, height, filename);
						});
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error when process album [{}]", albumTitle, e);
			}
		}
	}

	private boolean shouldDownloadAlbum(String albumId, String albumTitle,
	        boolean forced) {
		if (forced) {
			return true;
		}
		AlbumStatus status = albumService.albumLocalStatus(albumId);
		return status == null || status == AlbumStatus.DOWNLOADING ? true
		        : false;
	}

}
