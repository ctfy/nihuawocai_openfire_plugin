package com.carey.qq.openoauth.http;

import static com.carey.qq.openoauth.encoders.URL.queryString;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Request {

	private static final String CONTENT_LENGTH = "Content-Length";

	private String url;
	private Verb verb;
	private Map<String, String> bodyParams;
	private Map<String, String> headers;
	private String payload = null;

	public Request(Verb verb, String url) {
		this.verb = verb;
		this.url = url;
		this.bodyParams = new HashMap<String, String>();
		this.headers = new HashMap<String, String>();
	}

	public Response send(Object proxy) {
		try {
			return doSend(proxy);
		} catch (IOException ioe) {
			throw new RuntimeException("Problems while creating connection",
					ioe);
		}
	}

	Response doSend(Object proxy) throws IOException {
		HttpURLConnection connection;
		String str = this.headers.get("Authorization");
		if (str != null) {
			String hs[] = str.split(",");
			if (hs[0].startsWith("OAuth ")) {
				hs[0] = hs[0].substring("OAuth ".length());
			}

			String newUrl = url + "?";
			for (int i = 0; i < hs.length; i++) {
				hs[i] = hs[i].trim().replace("\"", "");
				if (i == hs.length - 1) {
					newUrl += hs[i];
				} else {
					newUrl += hs[i] + "&";
				}
			}
			System.out.println("newUrl=" + newUrl);

			if (proxy != null) {
				connection = (HttpURLConnection) new URL(newUrl)
						.openConnection();
			} else {
				connection = (HttpURLConnection) new URL(newUrl)
						.openConnection();
			}
			connection.setRequestMethod(this.verb.name());

			if (verb.equals(Verb.PUT) || verb.equals(Verb.POST)) {
				addBody(connection, getBodyContents());
			}
			return new Response(connection);
		}
		// ////////////////////////////////////////

		if (proxy != null) {
			connection = (HttpURLConnection) new URL(url).openConnection();
		} else {
			connection = (HttpURLConnection) new URL(url).openConnection();
		}
		connection.setRequestMethod(this.verb.name());
		addHeaders(connection);
		if (verb.equals(Verb.PUT) || verb.equals(Verb.POST)) {
			addBody(connection, getBodyContents());
		}
		return new Response(connection);

	}

	void addHeaders(HttpURLConnection conn) {
		for (String key : headers.keySet()) {
			conn.setRequestProperty(key, headers.get(key));
		}
	}

	void addBody(HttpURLConnection conn, String content) throws IOException {
		conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(content
				.getBytes().length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(content.getBytes());
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public void addBodyParameter(String key, String value) {
		this.bodyParams.put(key, value);
	}

	public void addPayload(String payload) {
		this.payload = payload;
	}

	public Set<Map.Entry<String, String>> getQueryStringParams() {
		try {
			Map<String, String> params = new HashMap<String, String>();
			String query = new URL(url).getQuery();
			if (query != null) {
				for (String param : query.split("&")) {
					String pair[] = param.split("=");
					params.put(pair[0], pair[1]);
				}
			}
			return params.entrySet();
		} catch (MalformedURLException mue) {
			throw new RuntimeException("Malformed URL", mue);
		}
	}

	public Set<Map.Entry<String, String>> getBodyParams() {
		return bodyParams.entrySet();
	}

	public String getUrl() {
		return url;
	}

	public String getSanitizedUrl() {
		return url.replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
	}

	public String getBodyContents() {
		return (payload != null) ? payload : queryString(bodyParams)
				.replaceFirst("\\?", "");
	}

	public Verb getVerb() {
		return verb;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public static enum Verb {
		GET, POST, PUT, DELETE
	}
}