package com.welocally.oauth.service;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = "session")
public class SessionService {
	private static final String SCOPE = "https://spreadsheets.google.com/feeds";
	private OAuthService service;
	private Token authToken;
	private Token accessToken;
	@Value("${googleApiKey}")
	String googleApiKey;
	@Value("${googleApiSecret}")
	String googleApiSecret;
	public String getOAuthorizationURL(String forUrl) {
		service = new ServiceBuilder().provider(GoogleApi.class)
				.apiKey(googleApiKey)
				.apiSecret(googleApiSecret)
				.scope(SCOPE)
				.callback(forUrl)
				.build();
		authToken = service.getRequestToken();
		String authUrl = service.getAuthorizationUrl(authToken);
		return authUrl;
	}
	public Token getAuthToken() {
		return authToken;
	}
	public void authorizeWith(String oauth_verifier) {
		accessToken = service.getAccessToken(authToken, new Verifier(oauth_verifier));		
	}
	public Token getAccessToken() {
		return accessToken;
	}
	
	public String retrieveWorksheets(String spreadsheetName) {
		 OAuthRequest request = new OAuthRequest(Verb.GET, "https://spreadsheets.google.com/feeds/worksheets/" + spreadsheetName + "/private/full");
		 service.signRequest(accessToken, request);
		 Response response = request.send();
		 return response.getBody();		 
	}
}
