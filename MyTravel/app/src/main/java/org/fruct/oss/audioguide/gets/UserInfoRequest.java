package org.fruct.oss.audioguide.gets;

import android.util.Xml;

import org.fruct.oss.audioguide.parsers.IContent;
import org.fruct.oss.audioguide.parsers.UserInfoParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class UserInfoRequest  extends GetsRequest{


    public UserInfoRequest(Gets gets){
        super(gets);
    }

    @Override
    protected String createRequestString() {
        return createUserInfoRequest();
    }

    @Override
    protected String getRequestUrl() {
        return Gets.GETS_SERVER + "/userInfo.php";
    }

    @Override
    protected Class<? extends IContent> getContentClass() {
        return UserInfoParser.class;
    }

    @Override
    protected void onError() {

    }

    private String createUserInfoRequest() {
        // TODO: apply timeStr field
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);

            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "request").startTag(null, "params");
            gets.writeTokenTag(serializer);
            serializer.endTag(null, "params").endTag(null, "request").endDocument();
            serializer.flush();

            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
