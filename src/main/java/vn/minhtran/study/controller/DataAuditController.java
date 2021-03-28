package vn.minhtran.study.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.minhtran.study.model.IntegrityCheckResponse;
import vn.minhtran.study.service.AlbumService;

@RestController
@RequestMapping("/audit")
public class DataAuditController {
	
	@Autowired
	private AlbumService albumService;

	@GetMapping("/albums/integrity")
	public List<IntegrityCheckResponse> checkDownloadIntegrity() {
		final List<IntegrityCheckResponse> ret = new ArrayList<>();
		
		
		
		return ret;
	}
}
