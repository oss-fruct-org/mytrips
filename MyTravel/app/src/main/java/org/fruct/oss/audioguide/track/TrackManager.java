package org.fruct.oss.audioguide.track;

import android.location.Location;

import org.fruct.oss.audioguide.gets.Category;
import org.fruct.oss.audioguide.util.Utils;

import java.io.Closeable;
import java.util.List;

public interface TrackManager extends Closeable {
	public static final String PREF_TRACK_MODE = "pref_track_mode";

	void insertPoint(Point point);

	void insertTrack(Track track);

	void insertToTrack(Track track, Point point, int selectedPosition);

    void insertToTrack(Track track, Point point);

	void storeTrackLocal(Track track);

	void requestTracksInRadius();

	void requestPointsInRadius(float latitude, float longitude, boolean autoStore);

	void requestPointsInTrack(Track track);

	void activateTrackMode(Track track);

	void addListener(TrackListener listener);

	void removeListener(TrackListener listener);

	void close();

	CursorHolder loadTracks();

	CursorHolder loadPrivateTracks();

	CursorHolder loadLocalTracks();

	CursorHolder loadLocalPoints();

	CursorHolder loadPoints(Track track);

	CursorHolder loadRelations();


	Track getTrackByName(String name);

    Track getTrackByHName(String hname);

	void updateUserLocation(Location location);

	void updateLoadRadius(float radius);


	List<Category> getCategories();

	void setCategoryState(Category category, boolean isActive);

	void deleteTrack(Track track, boolean deleteFromServer);

    public void removeFromTrack(Track track, Point point, int position);

    public void editPosition(Track track, Point point, int position, int prevPosition);

    public List<String> getTrackNames();

    public void publishTrack(Track track);

    public void unpublishTrack(Track track);

    public void getUserInfo(final Utils.UserInfoCallback<String, String> callback);

    public void deletePoint(Point point, boolean deleteFromServer);

}
