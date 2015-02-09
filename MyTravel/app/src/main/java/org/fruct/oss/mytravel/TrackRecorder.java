package org.fruct.oss.mytravel;


import org.fruct.oss.audioguide.track.DefaultTrackManager;
import org.fruct.oss.audioguide.track.Point;
import org.fruct.oss.audioguide.track.Track;
import org.fruct.oss.audioguide.track.TrackManager;

public class TrackRecorder {

    private static TrackRecorder instance;
    private static Track track;
    private static TrackManager trackManager;

    private static int count = 0;

    public TrackRecorder(){
        trackManager = DefaultTrackManager.getInstance();
    }

    public static synchronized  TrackRecorder getInstance() {
        if( instance == null ){
            instance = new TrackRecorder();
        }
        return instance;
    }


    public boolean isTrackSet(){
        return track != null;
    }

    public void pointVisited(Point p){
        trackManager.insertToTrack(track, p, count);
    }

    public void setTrack(Track t){
        track = t;
        count = 0;
    }

    public void stopRecording(){
        track = null;
    }

    public Track getTrack(){
        return track;
    }
}
