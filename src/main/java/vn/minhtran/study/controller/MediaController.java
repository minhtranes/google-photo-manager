package vn.minhtran.study.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.minhtran.study.model.MediaDownloadResponse;

@RestController
@RequestMapping("/media")
public class MediaController {

	@GetMapping("/download")
	public MediaDownloadResponse download() {
		MediaDownloadResponse ret = new MediaDownloadResponse();
		return ret;
	}
}
