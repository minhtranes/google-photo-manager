package vn.minhtran.study.service;

public interface MediaService {

	void downloadPhoto(String baseUrl, String albumName, String albumTitle,
	        String width, String height, String filename);
}
