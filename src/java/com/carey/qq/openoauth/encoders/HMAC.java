package com.carey.qq.openoauth.encoders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * An utility class for HMAC-SHA1 signature methods.
 * 
 * @author Pablo Fernandez
 */
public class HMAC {

	private static final String UTF8 = "UTF-8";
	private static final String HMAC_SHA1 = "HmacSHA1";

	/**
	 * Creates a Base64-encoded MAC-SHA1 for a given string.
	 * 
	 * @param String
	 *            to sign
	 * @param Key
	 *            used to sign the string (usually a shared secret)
	 * @return HMAC-SHA1 base64-encoded signature
	 * @throws RuntimeException
	 *             if the signature cannot be calculated.
	 */
	public static String sign(String toSign, String key) {
		try {
			return doSign(toSign, key);
		} catch (Exception e) {
			throw new RuntimeException("Error while signing string: " + toSign, e);
		}
	}

	private static String doSign(String toSign, String keyString) throws Exception {
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes(UTF8), HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(toSign.getBytes(UTF8));
		return new String(Base64Encoder.encode(bytes)).replace("\r\n", "");
	}
}
