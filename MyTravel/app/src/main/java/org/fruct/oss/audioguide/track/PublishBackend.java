package org.fruct.oss.audioguide.track;


public interface PublishBackend {
    void publishTrack(Track track);

    void unpublishTrack(Track track);

    void getUserInfo();
}
