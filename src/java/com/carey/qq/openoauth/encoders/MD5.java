package com.carey.qq.openoauth.encoders;

import java.security.MessageDigest;

/**
 * Utility class to generate MD5 hashes
 * 
 * @author Pablo Fernandez
 */
public class MD5 {

	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

	/**
	 * Calculates an MD5 hash
	 * <em>based on the {@link Object#toString()} method</em>.
	 * 
	 * @param any
	 *            object
	 * @return MD5 hash
	 */
	public static String hexHash(Object obj) {
		String toHash = obj.toString();
		try {
			MessageDigest dg = MessageDigest.getInstance("MD5");
			dg.update(toHash.getBytes("UTF-8"));
			return bytesToHex(dg.digest());
		} catch (Exception e) {
			throw new RuntimeException("Error while hashing string: " + toHash,
					e);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; ++i) {
			int iByte = bytes[i] & 0xff;
			chars[i * 2 + 0] = HEX_DIGITS[iByte >> 4];
			chars[i * 2 + 1] = HEX_DIGITS[iByte & 0x0f];
		}
		return new String(chars);
	}
}
