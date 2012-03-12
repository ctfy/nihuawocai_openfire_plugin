package com.carey.qq.openoauth.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Response {

	private static final String ENCODING = "UTF-8";
	private static final String EMPTY = "";

	private int code;
	private String body;
	private InputStream stream;
	private Map<String, String> headers;

	Response(HttpURLConnection connection) throws IOException {
		try {
			connection.connect();
			this.code = connection.getResponseCode();
			this.headers = parseHeaders(connection);
			this.stream = wasSuccessful() ? connection.getInputStream()
					: connection.getErrorStream();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			this.code = 404;
			this.body = EMPTY;
		}
	}

	private String parseBodyContents() {
		try {
			final char[] buffer = new char[0x10000];
			StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(stream, ENCODING);
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);
			in.close();
			body = out.toString();
			return body;
		} catch (IOException ioe) {
			throw new RuntimeException("Error while reading response body", ioe);
		}
	}

	private Map<String, String> parseHeaders(HttpURLConnection conn) {
		Map<String, String> headers = new HashMap<String, String>();
		for (String key : conn.getHeaderFields().keySet()) {
			headers.put(key, conn.getHeaderFields().get(key).get(0));
		}
		return headers;
	}

	private boolean wasSuccessful() {
		return code >= 200 && code < 400;
	}

	public String getBody() {
		return body != null ? body : parseBodyContents();
	}

	public InputStream getStream() {
		return stream;
	}

	public int getCode() {
		return code;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getHeader(String name) {
		return headers.get(name);
	}

	public void dumpResponse() {
		System.out.println("-------------Response---------------");
		System.out.println("code: " + code);
		System.out.println("body: " + body);

		System.out.println("headers: ");
		for (String key : headers.keySet()) {
			System.out.println("[key] " + key + " , [value] "
					+ headers.get(key));
		}
		System.out.println("************");

		System.out.println("-----------------------------------");
	}

}