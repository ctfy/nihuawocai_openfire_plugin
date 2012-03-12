package com.carey.qq.openoauth.oauth;

public class Token {

	private String token;
	private String secret;
	private String rawString;

	public Token(String token, String secret) {
		this(token, secret, "");
	}

	public Token(String token, String secret, String rawString) {
		this.token = token;
		this.secret = secret;
		this.rawString = rawString;
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

	public String getRawString() {
		return rawString;
	}

	public String toString() {
		return String.format("oauth_token -> %s oauth_token_secret -> %s",
				token, secret);
	}
}
