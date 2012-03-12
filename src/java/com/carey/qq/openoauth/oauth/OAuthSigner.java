package com.carey.qq.openoauth.oauth;

import java.util.Map;

import com.carey.qq.openoauth.encoders.HMAC;
import com.carey.qq.openoauth.encoders.URL;
import com.carey.qq.openoauth.eq.DefaultEqualizer;
import com.carey.qq.openoauth.http.Request;

public class OAuthSigner {

	private final String consumerSecret;
	private final OAuthParameters params;
	private final DefaultEqualizer eq;

	public OAuthSigner(String consumerKey, String consumerSecret,
			DefaultEqualizer eq) {
		this.consumerSecret = consumerSecret;
		this.eq = eq;
		this.params = new OAuthParameters(consumerKey);
	}

	public void signForRequestToken(Request request, String callback) {
		params.put(OAuth.CALLBACK, callback);

		String toSign = getStringToSign(request, CallType.REQUEST_TOKEN);
		String oAuthHeader = getOAuthHeader(request, toSign,
				OAuth.EMPTY_TOKEN_SECRET, CallType.REQUEST_TOKEN);

		request.addHeader(OAuth.HEADER, oAuthHeader);
	}

	public void signForAccessToken(Request request, Token requestToken,
			String verifier) {
		params.put(OAuth.TOKEN, requestToken.getToken());
		params.put(OAuth.VERIFIER, verifier);

		String toSign = getStringToSign(request, CallType.ACCESS_TOKEN);
		String oAuthHeader = getOAuthHeader(request, toSign, requestToken
				.getSecret(), CallType.ACCESS_TOKEN);

		request.addHeader(OAuth.HEADER, oAuthHeader);
	}

	public void sign(Request request, Token accessToken) {
		params.put(OAuth.TOKEN, accessToken.getToken());

		String toSign = getStringToSign(request, CallType.RESOURCE);
		String oAuthHeader = getOAuthHeader(request, toSign, accessToken
				.getSecret(), CallType.RESOURCE);
		request.addHeader(OAuth.HEADER, oAuthHeader);
	}

	public String getOAuthHeader(Request request, String toSign,
			String tokenSecret, CallType type) {
		String signature = getSignature(toSign, tokenSecret);
		params.put(OAuth.SIGNATURE, signature);
		return eq.tuneOAuthHeader(request, params.asOAuthHeader(), type);
	}

	public String getSignature(String toSign, String tokenSecret) {
		return HMAC.sign(toSign, consumerSecret + '&' + tokenSecret);
	}

	public String getStringToSign(Request request, CallType type) {
		String verb = URL.percentEncode(request.getVerb().name());
		String url = URL.percentEncode(request.getSanitizedUrl());
		addQueryStringParams(request);
		addBodyParams(request);
		String sortedParams = URL.percentEncode(params
				.asSortedFormEncodedString());
		return eq.tuneStringToSign(request, String.format("%s&%s&%s", verb,
				url, sortedParams), type);
	}

	private void addQueryStringParams(Request request) {
		for (Map.Entry<String, String> entry : request.getQueryStringParams()) {
			params.put(entry.getKey(), entry.getValue());
		}
	}

	private void addBodyParams(Request request) {
		for (Map.Entry<String, String> entry : request.getBodyParams()) {
			params.put(entry.getKey(), entry.getValue());
		}
	}
}