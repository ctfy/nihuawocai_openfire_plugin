package com.carey.qq.openoauth.oauth;

import java.net.Proxy;
import java.util.Properties;

import com.carey.qq.openoauth.eq.DefaultEqualizer;
import com.carey.qq.openoauth.http.Request;
import com.carey.qq.openoauth.http.Response;
import com.carey.qq.openoauth.http.Request.Verb;

/**
 * The main class. Provides an access point for all OAuth methods.
 * 
 * Please get an instance normally, using the public constructor. The
 * {@link Scribe#getInstance(Properties)} method is kept for compatibility
 * reasons but will be removed eventually
 * 
 * @author Pablo Fernandez
 */
public class Scribe {

	private static final String EQUALIZER = "scribe.equalizer";
	private static final String REQUEST_TOKEN_URL = "request.token.url";
	private static final String CALLBACK_URL = "callback.url";
	private static final String REQUEST_TOKEN_VERB = "request.token.verb";
	private static final String ACCESS_TOKEN_URL = "access.token.url";
	private static final String ACCESS_TOKEN_VERB = "access.token.verb";
	private static final String CONSUMER_SECRET = "consumer.secret";
	private static final String CONSUMER_KEY = "consumer.key";
	private static Scribe instance;

	private final Properties config;
	private DefaultEqualizer eq;

	/**
	 * Factory method
	 * 
	 * @deprecated Use the default constructor instead. If you need to enforce a
	 *             single instance do so with your own code.
	 * @param Scribe
	 *            properties
	 * @return The Scribe single instance
	 */
	@Deprecated
	public static synchronized Scribe getInstance(Properties props) {
		if (instance == null) {
			instance = new Scribe(props);
		}
		return instance;
	}

	/**
	 * Default constructor
	 * 
	 * @param Scribe
	 *            properties
	 */
	public Scribe(Properties props) {
		this.config = props;
		initEqualizer();
	}

	private void initEqualizer() {
		String eqName = config.getProperty(EQUALIZER);
		if (isEmpty(eqName)) {
			this.eq = new DefaultEqualizer();
		} else
			try {
				this.eq = (DefaultEqualizer) Class.forName(eqName)
						.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(
						"Could not initalize Scribe equalizer", e);
			}
	}

	private boolean isEmpty(String string) {
		return (string == null || string.trim().length() <= 0);
	}

	/**
	 * Obtains the request token and token secret.
	 * 
	 * @return the request token
	 */
	public Token getRequestToken(Proxy proxy) {
		Request request = getRTRequest();
		OAuthSigner signer = getOAuthSigner();
		signer.signForRequestToken(request, config.getProperty(CALLBACK_URL));
		this.eq.tuneRequest(request, CallType.REQUEST_TOKEN);
//		request.d();
		Response response = request.send(proxy);
		String responseBody = response.getBody();
		response.dumpResponse();
		return eq.parseRequestTokens(responseBody);
	}

	private Request getRTRequest() {
		return new Request(
				Verb.valueOf(config.getProperty(REQUEST_TOKEN_VERB)), config
						.getProperty(REQUEST_TOKEN_URL));
	}

	private OAuthSigner getOAuthSigner() {
		return new OAuthSigner(config.getProperty(CONSUMER_KEY), config
				.getProperty(CONSUMER_SECRET), eq);
	}

	/**
	 * Obtains the access token and access token secret.
	 * 
	 * @param request
	 *            token
	 * @param verifier
	 * @return access token
	 */
	public Token getAccessToken(Token token, String verifier, Proxy proxy) {
		Request request = getATRequest();
		OAuthSigner signer = getOAuthSigner();
		signer.signForAccessToken(request, token, verifier);
		eq.tuneRequest(request, CallType.ACCESS_TOKEN);
		Response response = request.send(proxy);
		return eq.parseAccessTokens(response.getBody());
	}

	private Request getATRequest() {
		return new Request(Verb.valueOf(config.getProperty(ACCESS_TOKEN_VERB)),
				config.getProperty(ACCESS_TOKEN_URL));
	}

	/**
	 * Adds an OAuth header to the {@link Request}
	 * 
	 * @param Request
	 *            to sign
	 * @param access
	 *            token
	 */
	public void signRequest(Request request, Token token) {
		OAuthSigner signer = getOAuthSigner();
		signer.sign(request, token);
		eq.tuneRequest(request, CallType.RESOURCE);
	}

	/**
	 * Returns useful debug information about the {@link Request}, JSON style.
	 * 
	 * @param Request
	 *            to inspect
	 * @param access
	 *            token
	 * @return request information (JSON)
	 */
	public String getJsonInfo(Request request, Token token) {
		return RequestInspector.getJsonInfo(getOAuthSigner(), request, token);
	}
}