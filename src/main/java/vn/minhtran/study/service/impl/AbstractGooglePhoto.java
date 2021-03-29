package vn.minhtran.study.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext.Builder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import vn.minhtran.study.infra.cache.RestorableCache;
import vn.minhtran.study.infra.persistence.entity.AlbumEntity;

abstract class AbstractGooglePhoto
        extends RestorableCache<String, AlbumEntity> {

	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	private OAuth2AuthorizedClientProvider provider = new RefreshTokenOAuth2AuthorizedClientProvider();

	protected final String accessToken() {
		Authentication authentication = SecurityContextHolder.getContext()
		        .getAuthentication();

		OAuth2AuthorizedClient authorizedClient = null;
		String name = authentication.getName();

		synchronized (name) {
			authorizedClient = authorizedClientService
			        .loadAuthorizedClient("google", name);

			authorizedClient = refreshIfNeccessary(authorizedClient,
			        authentication);
		}

		OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
		String tokenString = accessToken != null ? accessToken.getTokenValue()
		        : "NULL";

		return tokenString;
	}

	private OAuth2AuthorizedClient refreshIfNeccessary(
	        OAuth2AuthorizedClient authorizedClient, Authentication principal) {
		Builder contextBuilder = OAuth2AuthorizationContext
		        .withAuthorizedClient(authorizedClient);
		OAuth2AuthorizationContext authorizationContext = contextBuilder
		        .principal(principal).build();
		OAuth2AuthorizedClient authorizedC = provider
		        .authorize(authorizationContext);
		if (authorizedC != null) {
			authorizedClientService.saveAuthorizedClient(authorizedC,
			        principal);
			return authorizedC;
		}

		return authorizedClient;
	}
}
