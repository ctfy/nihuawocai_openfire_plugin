package com.carey.qq.openoauth.eq;

import com.carey.qq.openoauth.http.Request;
import com.carey.qq.openoauth.oauth.CallType;

/**
 * A custom equalizer that works with LinkedIn OAuth.
 * 
 * @author Pablo Fernandez
 */
public class LinkedInEqualizer extends DefaultEqualizer {

	private static final String TILDE_DECODED = "~";
	private static final String SEPARATOR = "&";
	private static final String TILDE_ENCODED = "%7E";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String tuneStringToSign(Request request, String toSign, CallType type) {
		if (type != CallType.RESOURCE) {
			return toSign;
		} else {
			return decodeURLTilde(toSign);
		}
	}

	private String decodeURLTilde(String toSign) {
		String toSignParts[] = toSign.split(SEPARATOR);

		String verb = toSignParts[0];
		String url = toSignParts[1];
		String params = toSignParts[2];

		url = url.replace(TILDE_ENCODED, TILDE_DECODED);

		return verb + SEPARATOR + url + SEPARATOR + params;
	}
}
