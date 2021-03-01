package vn.minhtran.study.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import vn.minhtran.study.service.AlbumService;
import vn.minhtran.study.service.MediaService;

@RestController
@RequestMapping("albums")
public class AlbumController {

	private static Logger LOGGER = LoggerFactory
			.getLogger(AlbumController.class);

	@Autowired
	private AlbumService albumService;

	@Autowired
	private MediaService mediaService;

	@GetMapping("/list")
	public String listAlbums(Authentication authentication) throws IOException {
		JsonNode albums = albumService.list();
		JsonNode albumsCon = albums.findValue("albums");
		if (albumsCon.isArray()) {
			for (JsonNode al : albumsCon) {
				JsonNode idValueNode = al.findValue("id");
				String id = idValueNode.textValue();
				JsonNode titleNode = al.findValue("title");
				String title = titleNode.textValue();

				LOGGER.info("Reading album {}...", title);
				try {
					JsonNode albumContent = albumService.albumContent(id);
					JsonNode mediaItemsCon = albumContent
							.findValue("mediaItems");
					if (mediaItemsCon.isArray()) {
						for (JsonNode mcj : mediaItemsCon) {
							String baseUrl = mcj.findValue("baseUrl")
									.textValue();
							String filename = mcj.findValue("filename")
									.textValue();
							JsonNode mediaMetadata = mcj
									.findParent("mediaMetadata");
							String width = mediaMetadata.findValue("width")
									.textValue();
							String height = mediaMetadata.findValue("height")
									.textValue();
							baseUrl = String.format("%s=w%s-h%s", baseUrl,
									width, height);
							LOGGER.info("Download file [{}]...", filename);
							mediaService.downloadPhoto(baseUrl, title, width,
									height, filename);
						}
					}
				} catch (Exception e) {
					LOGGER.error("Error when process album [{}]", title, e);
				}
			}
		}

		return null;
	}

}
