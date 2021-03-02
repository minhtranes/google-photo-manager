package vn.minhtran.study.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import vn.minhtran.study.infra.cache.RestorableCache;
import vn.minhtran.study.infra.persistence.entity.AlbumEntity;

abstract class AbstractGooglePhoto
		extends
			RestorableCache<String, AlbumEntity> {

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	protected final String accessToken() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		String name = authentication.getName();
		OAuth2AuthorizedClient authorizedClient = authorizedClientService
				.loadAuthorizedClient("google", name);
		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		String tokenString = accessToken != null
				? accessToken.getTokenValue()
				: "NULL";

		return tokenString;
	}
}
