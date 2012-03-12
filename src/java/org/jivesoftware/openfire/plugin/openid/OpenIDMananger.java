package org.jivesoftware.openfire.plugin.openid;

public class OpenIDMananger {

	public static final String TYPE_QQ = "qq";

	public static String getOpenID(String type, String token, String tokenSecret) {
		IOpenID openID = getOpenID(type);
		return openID.getOpenID(token, tokenSecret);
	}

	public static IOpenID getOpenID(String type) {
		if (TYPE_QQ.equals(type)) {
			return new QQ();

		}
		return null;
	}
}
