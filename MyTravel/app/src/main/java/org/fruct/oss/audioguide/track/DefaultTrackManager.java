package org.fruct.oss.audioguide.track;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;

import org.fruct.oss.audioguide.App;
import org.fruct.oss.audioguide.config.Config;
import org.fruct.oss.audioguide.files.DefaultFileManager;
import org.fruct.oss.audioguide.files.FileManager;
import org.fruct.oss.audioguide.gets.Category;
import org.fruct.oss.audioguide.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultTrackManager implements TrackManager, Closeable {
    private final static Logger log = LoggerFactory.getLogger(DefaultTrackManager.class);
	private final StorageBackend backend;
	private final CategoriesBackend categoriesBackend;
	private final Database database;
	private final FileManager fileManager;
	private final SharedPreferences pref;

	private List<Category> categories;
	private List<Category> activeCategories;
    private List<String> trackNames;

	private final List<TrackListener> listeners = new ArrayList<TrackListener>();
	private final List<CursorHolder> cursorHolders = new ArrayList<CursorHolder>();

	private final SynchronizerThread synchronizer;

	private Location location = new Location("no-provider");
	private float radius;

	public DefaultTrackManager(Context context, StorageBackend backend, CategoriesBackend catBackend) {
		this.categoriesBackend = catBackend;
		this.backend = backend;

		pref = PreferenceManager.getDefaultSharedPreferences(context);

		fileManager = DefaultFileManager.getInstance();

		database = new Database(context);

		if (!Config.isEditLocked()) {
			synchronizer = new SynchronizerThread(database, backend);
			synchronizer.start();
			synchronizer.initializeHandler();
		} else {
			synchronizer = null;
		}
	}

	@Override
	public synchronized void close() {
		if (synchronizer != null) {
			synchronizer.interrupt();
			synchronizer.quit();
		}

		database.close();
		instance = null;
	}

	@Override
	public void insertPoint(Point point) {
		database.insertPoint(point);
		database.markPointUpdate(point);
		notifyDataChanged();
	}

	@Override
	public void insertTrack(Track track) {
		database.insertTrack(track);
		database.markTrackUpdate(track);
		notifyDataChanged();
	}

	@Override
	public void insertToTrack(Track track, Point point, int selectedPosition) {
		database.insertToTrack(track, point, selectedPosition);
		database.markTrackUpdate(track);
		notifyDataChanged();
	}

    @Override
    public void insertToTrack(Track track, Point point) {
        database.insertToTrack(track, point);
        database.markTrackUpdate(track);
        notifyDataChanged();
    }

    public void removeFromTrack(Track track, Point point, int position){
        database.removeFromTrack(track, point, position);
        database.markTrackUpdate(track);
        notifyDataChanged();
    }

    public void editPosition(Track track, Point point, int newPosition, int prevPosition){
        log.error("ChangedPosition");
        database.removeFromTrack(track, point, prevPosition);
        database.insertToTrack(track, point, newPosition);
        database.markTrackUpdate(track);
        notifyDataChanged();
    }

	@Override
	public void storeTrackLocal(final Track track) {
		backend.loadPointsInTrack(track, new Utils.Callback<List<Point>>() {
			@Override
			public void call(List<Point> points) {
				track.setLocal(true);
				database.insertTrack(track);

				for (Point point : points) {
					point.setPrivate(track.isPrivate());

					if (point.getCategoryId() == -1)
						point.setCategoryId(track.getCategoryId());
/*
					if (point.hasAudio()) {
						fileManager.insertRemoteFile("no-title", Uri.parse(point.getAudioUrl()));
						fileManager.requestAudioDownload(point.getAudioUrl());
					}*/
				}

				database.insertPointsToTrack(track, points);
				notifyDataChanged();
			}
		});
	}

	@Override
	public void requestTracksInRadius() {
		loadRemoteCategories();
		backend.loadTracksInRadius((float) location.getLatitude(), (float) location.getLongitude(), radius, activeCategories, new Utils.Callback<List<Track>>() {
			@Override
			public void call(List<Track> tracks) {
				for (Track track : tracks) {
					//track.setLocal(false);
					database.insertTrack(track);
                    storeTrackLocal(track);
				}

				notifyDataChanged();
			}
		});
	}

	@Override
	public void requestPointsInRadius(final float latitude, final float longitude, boolean autoStore) {
		backend.loadPointsInRadius(latitude, longitude, radius, activeCategories, new Utils.Callback<List<Point>>() {
			@Override
			public void call(List<Point> points) {
				for (Point point : points) {
					if (point.hasAudio()) {
						fileManager.insertRemoteFile("no-title", Uri.parse(point.getAudioUrl()));
						fileManager.requestAudioDownload(point.getAudioUrl());
					}

					database.insertPoint(point);
				}

				notifyDataChanged();
			}
		});
	}

	@Override
	public void requestPointsInTrack(final Track track) {
		backend.loadPointsInTrack(track, new Utils.Callback<List<Point>>() {
			@Override
			public void call(List<Point> points) {
				if (points == null)
					return;

				for (Point point : points) {
					if (point.hasAudio()) {
						fileManager.insertRemoteFile("no-title", Uri.parse(point.getAudioUrl()));
						fileManager.requestAudioDownload(point.getAudioUrl());
					}

					point.setPrivate(track.isPrivate());
				}

				database.insertPointsToTrack(track, points);

				notifyDataChanged();
			}
		});
	}

	@Override
	public void activateTrackMode(Track track) {
		if (track == null)
			pref.edit().remove(PREF_TRACK_MODE).apply();
		else
			pref.edit().putString(PREF_TRACK_MODE, track.getName()).apply();
	}

	@Override
	public void addListener(TrackListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(TrackListener listener) {
		listeners.remove(listener);
	}

	@Override
	public CursorHolder loadTracks() {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadTracksCursor();
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;
	}


	@Override
	public CursorHolder loadPrivateTracks() {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadPrivateTracks();
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;
	}

	@Override
	public CursorHolder loadLocalTracks() {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadLocalTracks();
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;
	}

	@Override
	public CursorHolder loadLocalPoints() {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadPointsCursor();
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;

	}

	@Override
	public CursorHolder loadPoints(final Track track) {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadPointsCursor(track);
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;
	}

	@Override
	public CursorHolder loadRelations() {
		CursorHolder cursorHolder = new CursorHolder() {
			@Override
			protected Cursor doQuery() {
				return database.loadRelationsCursor();
			}
		};

		addCursorHolder(cursorHolder);
		cursorHolder.queryAsync();
		return cursorHolder;
	}

    public void publishTrack(Track track){
        backend.publishTrack(track);
    }

    public void unpublishTrack(Track track){
        backend.unpublishTrack(track);
    }

    public void getUserInfo(final Utils.UserInfoCallback<String, String> callback){
        backend.getUserInfo(new Utils.UserInfoCallback<String, String>() {
            @Override
            public void call(String a, String b) {
                callback.call(a,b);
            }
            });
    }

    @Override
    public void deletePoint(Point point, boolean deleteFromServer) {
        if( deleteFromServer && point.isPrivate()){
            backend.deletePoint(point, new Utils.Callback<Point>() {
                @Override
                public void call(Point point) {
                    database.deletePoint(point);
                    notifyDataChanged();
                }
            });
        }else{
            database.deletePoint(point);
            notifyDataChanged();
        }
    }

    @Override
	public Track getTrackByName(String name) {
		if (name == null)
			return null;
		return database.getTrackByName(name);
	}

    @Override
    public Track getTrackByHName(String hname) {
        if (hname == null)
            return null;
        return database.getTrackByHName(hname);
    }

	@Override
	public void updateUserLocation(Location location) {
		this.location = location;
	}

	@Override
	public void updateLoadRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public List<Category> getCategories() {
		if (categories == null) {
			categories = database.getCategories();
			activeCategories = database.getActiveCategories();
			loadRemoteCategories();
		}

		return categories;
	}

    @Override
    public List<String> getTrackNames(){
        trackNames = database.getTrackNames();
        return trackNames;
    }

	@Override
	public void setCategoryState(Category category, boolean isActive) {
		category.setActive(isActive);
		database.setCategoryState(category);

		for (Category cat : categories) {
			if (category.getId() == cat.getId()) {
				cat.setActive(isActive);
			}
		}

		activeCategories = database.getActiveCategories();
		requestTracksInRadius();
	}

	@Override
	public void deleteTrack(Track track, boolean deleteFromServer) {
		if (deleteFromServer && track.isPrivate()) {
			backend.deleteTrack(track, new Utils.Callback<Track>() {
				@Override
				public void call(Track track) {
					database.deleteTrack(track);
					notifyDataChanged();
				}
			});
		} else {
			database.deleteTrack(track);
		}
	}


	private void loadRemoteCategories() {
		categoriesBackend.loadCategories(new Utils.Callback<List<Category>>() {
			@Override
			public void call(List<Category> categories) {
				DefaultTrackManager.this.categories = categories;
				database.updateCategories(categories);
				activeCategories = database.getActiveCategories();
				//requestTracksInRadius();

			}
		});
	}

	private void notifyDataChanged() {
		for (TrackListener listener : listeners) {
			listener.onDataChanged();
		}

		reQueryCursorHolders();
	}


	private CursorHolder addCursorHolder(CursorHolder cursorHolder) {
		cursorHolders.add(cursorHolder);
		return cursorHolder;
	}

	private void reQueryCursorHolders() {
		for (Iterator<CursorHolder> iterator = cursorHolders.iterator(); iterator.hasNext(); ) {
			CursorHolder holder = iterator.next();

			if (holder.isClosed()) {
				iterator.remove();
				continue;
			}

			holder.queryAsync();
		}
	}

	private static DefaultTrackManager instance;
	public synchronized static TrackManager getInstance() {
		if (instance == null) {
			GetsBackend backend = new GetsBackend();
			instance = new DefaultTrackManager(App.getContext(), backend, backend);
			instance.getCategories();
		}

		return instance;
	}
}
