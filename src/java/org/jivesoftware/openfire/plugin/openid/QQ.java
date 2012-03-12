package org.jivesoftware.openfire.plugin.openid;

import java.util.regex.Pattern;

import com.carey.qq.openoauth.eq.DefaultEqualizer;
import com.carey.qq.openoauth.http.Request;
import com.carey.qq.openoauth.http.Request.Verb;
import com.carey.qq.openoauth.http.Response;
import com.carey.qq.openoauth.oauth.OAuthSigner;
import com.carey.qq.openoauth.oauth.Token;

class QQ implements IOpenID {
	static String KEY = "bf89acb1576c4564bacf2f8d2a7a16c8", SECRET = "1d9a307041981ca12928902ff67cb089";

	@Override
	public String getOpenID(String token, String tokenSecret) {
		Token t = new Token(token, tokenSecret);
		Request r = new Request(Verb.GET, "http://open.t.qq.com/api/user/info");
		OAuthSigner signer = new OAuthSigner(KEY, SECRET, new DefaultEqualizer());
		signer.sign(r, t);
		Response p = r.send(null);
		String str = p.getBody();
		java.util.regex.Matcher m = Pattern.compile("\"openid\":\"(.{4,}?)\"").matcher(str);
		if (m.find()) {
			String openid = m.group(1);
			return openid;
		}
		return null;
	}

}
