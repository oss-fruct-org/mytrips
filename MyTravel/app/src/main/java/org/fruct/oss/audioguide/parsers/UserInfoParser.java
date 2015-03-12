package org.fruct.oss.audioguide.parsers;

import org.fruct.oss.audioguide.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class UserInfoParser implements IContent {
    private final static Logger log = LoggerFactory.getLogger(UserInfoParser.class);

    private String email;
    private boolean isTrusted;

    public String getEmail(){
        return email;
    }

    public boolean getTrusted(){
        return isTrusted;
    }

	public static IContent parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        String email = "";
        boolean isTrusted = false;

		parser.require(XmlPullParser.START_TAG, null, "content");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "userInfo");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG)
				continue;

			String tagName = parser.getName();
			if (tagName.equals("email")) {
                email = GetsResponse.readText(parser);
            }
            else if (tagName.equals("isTrustedUser"))
                isTrusted= Boolean.parseBoolean(GetsResponse.readText(parser));
			else {
				Utils.skip(parser);
			}
		}

		parser.require(XmlPullParser.END_TAG, null, "userInfo");
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, null, "content");

		UserInfoParser info = new UserInfoParser();
		info.isTrusted = isTrusted;
        info.email = email;

		return info;
	}


}
