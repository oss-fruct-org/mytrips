package org.fruct.oss.audioguide.gets;

import android.util.Xml;

import org.fruct.oss.audioguide.parsers.IContent;
import org.fruct.oss.audioguide.track.Point;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class DeletePointRequest extends GetsRequest{
    Point p;

    public DeletePointRequest(Gets gets, Point p){
        super(gets);
        this.p = p;
    }
    @Override
    protected String createRequestString() {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);

            serializer.startDocument("UTF-8", true);

            serializer.startTag(null, "request").startTag(null, "params");
            gets.writeTokenTag(serializer);
            serializer
            .startTag(null, "category_id").text(p.getCategoryId()+"").endTag(null, "category_id")
            .startTag(null, "name").text(p.getName()).endTag(null, "name")
                    .endTag(null, "params").endTag(null, "request").endDocument();
            serializer.flush();

            return writer.toString();
        } catch (IOException e) {
            return null;
        }

    }

    @Override
    protected String getRequestUrl() {
        return Gets.GETS_SERVER + "/deletePoint.php";
    }

    @Override
    protected Class<? extends IContent> getContentClass() {
        return null;
    }

    @Override
    protected void onError() {

    }
}
