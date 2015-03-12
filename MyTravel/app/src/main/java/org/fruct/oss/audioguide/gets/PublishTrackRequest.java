package org.fruct.oss.audioguide.gets;

import android.util.Xml;

import org.fruct.oss.audioguide.parsers.IContent;
import org.fruct.oss.audioguide.track.Track;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class PublishTrackRequest extends GetsRequest{

    Track track;
    public PublishTrackRequest(Gets gets,  Track track) {
        super(gets);

        this.track = track;
    }


    @Override
    protected String createRequestString() {
        return createPublishTrackRequest(track.getName(), track.getCategoryId());
    }

    @Override
    protected String getRequestUrl() {
        return Gets.GETS_SERVER + "/publish.php";
    }
    @Override
    protected boolean onPreExecute() {
        return gets.getEnv("token") != null;
    }

    @Override
    protected Class<? extends IContent> getContentClass() {
        return null;
    }

    @Override
    protected void onError() {

    }

    private String createPublishTrackRequest(String trackName, Long cat_id) {
        // TODO: apply timeStr field
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);

            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "request").startTag(null, "params");
            gets.writeTokenTag(serializer);

            serializer
                    .startTag(null, "track_name").text(trackName).endTag(null, "track_name")
                   // .startTag(null, "category_id").text(cat_id.toString()).endTag(null, "category_id")
                    .endTag(null, "params").endTag(null, "request").endDocument();
            serializer.flush();

            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
