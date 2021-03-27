package vn.minhtran.study.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.minhtran.study.model.IntegrityCheckResponse;

@RestController
@RequestMapping("/audit")
public class DataAuditController {

	@GetMapping("/albums/integrity")
	public List<IntegrityCheckResponse> checkDownloadIntegrity() {
		final List<IntegrityCheckResponse> ret = new ArrayList<>();

		return ret;
	}
}
