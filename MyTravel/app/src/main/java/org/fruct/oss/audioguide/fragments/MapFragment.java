package org.fruct.oss.audioguide.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import org.fruct.oss.audioguide.MultiPanel;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.config.Config;
import org.fruct.oss.audioguide.dialogs.EditPointDialog;
import org.fruct.oss.audioguide.dialogs.EditTrackDialog;
import org.fruct.oss.audioguide.dialogs.InsertPointDialog;
import org.fruct.oss.audioguide.dialogs.SelectTrackDialog;
import org.fruct.oss.audioguide.overlays.EditOverlay;
import org.fruct.oss.audioguide.overlays.MyPositionOverlay;
import org.fruct.oss.audioguide.preferences.SettingsActivity;
import org.fruct.oss.audioguide.track.CursorHolder;
import org.fruct.oss.audioguide.track.DefaultTrackManager;
import org.fruct.oss.audioguide.track.Point;
import org.fruct.oss.audioguide.track.Track;
import org.fruct.oss.audioguide.track.TrackManager;
import org.fruct.oss.audioguide.track.TrackingService;
import org.fruct.oss.mytravel.IntroDialog;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MapFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
	private final static Logger log = LoggerFactory.getLogger(MapFragment.class);

	private final static String PREF_LATITUDE = "pref-latitude";
	private final static String PREF_LONGITUDE = "pref-longitude";
	private final static String PREF_ZOOM = "pref-zoom";
	public static final String ARG_POINT = "point";

    public static boolean introShown = false;

	private MapView mapView;
	private TrackManager trackManager;
	private TrackingService trackingService;
	private TrackingServiceConnection serviceConnection = new TrackingServiceConnection();
	private SharedPreferences pref;


	private MyPositionOverlay myPositionOverlay;
	private BroadcastReceiver locationReceiver;
	private BroadcastReceiver pointInRangeReceiver;
    private EditOverlay freePointsOverlay;

	private ViewGroup bottomToolbar;
	private MultiPanel multiPanel;

    public static HashMap<String, Integer> trackColors;

	private Point selectedPoint;
    private IGeoPoint dragCoords;
    private IGeoPoint dragStart;
    private boolean dragging = false;

	private List<EditOverlay> trackOverlays = new ArrayList<EditOverlay>();
    public static final GeoPoint PTZ = new GeoPoint(61.783333, 34.350000);

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment MapFragment.
	 */
	public static MapFragment newInstance() {
		return new MapFragment();
	}
	public MapFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		log.trace("MapFragment onCreate");

		super.onCreate(savedInstanceState);

		trackManager = DefaultTrackManager.getInstance();
		setHasOptionsMenu(true);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pref.registerOnSharedPreferenceChangeListener(this);

        showIntroDialog();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.map_menu, menu);
		inflater.inflate(R.menu.categories_filter, menu);

		if (Config.isEditLocked()) {
			menu.findItem(R.id.action_add).setVisible(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_place:
			mockLocation();
			break;
		case R.id.action_add:
			startAddingPoint();
			break;
		case R.id.action_find_me:
			if (myPositionOverlay != null && myPositionOverlay.getLocation() != null) {
				GeoPoint newMapCenter = new GeoPoint(myPositionOverlay.getLocation());
				if (mapView.getZoomLevel() < 15) {
					mapView.getController().setZoom(15);
				}

				mapView.getController().animateTo(newMapCenter);
			}else{
                Toast.makeText(getActivity(), getResources().getString(R.string.position_not_set), Toast.LENGTH_SHORT).show();
            }
			break;
		case R.id.action_search:
            if(myPositionOverlay != null && myPositionOverlay.getLocation() != null) {
                startSearchingPoints();
            }else{
                Toast.makeText(getActivity(), getResources().getString(R.string.position_not_set), Toast.LENGTH_SHORT).show();
            }
			break;
		case R.id.action_stop_guide:
			if (pref.contains(TrackManager.PREF_TRACK_MODE)) {
				trackManager.activateTrackMode(null);
				updatePointsOverlay();
			} else {
				SelectTrackDialog dialog = SelectTrackDialog.newInstance();
				dialog.setListener(activateTrackListener);
                dialog.setNewTrackListener(showTrackDialogListener);
				FragmentTransaction trans = getFragmentManager().beginTransaction();
				trans.addToBackStack("select-track-dialog");
				dialog.show(trans, "select-track-dialog");
			}
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void startSearchingPoints() {
		Toast.makeText(getActivity(), getResources().getString(R.string.searching_points), Toast.LENGTH_LONG).show();

		trackManager.requestPointsInRadius((float) myPositionOverlay.getLocation().getLatitude(),
				(float) myPositionOverlay.getLocation().getLongitude(),
				true);
        trackManager.requestTracksInRadius();
	}

	private void startAddingPoint() {
		EditPointDialog dialog = EditPointDialog.newInstance(null);
		dialog.setListener(editDialogListener);
		dialog.show(getFragmentManager(), "edit-point-dialog");
	}

	private void mockLocation() {
		if (trackingService != null) {
			IGeoPoint mapCenter = mapView.getMapCenter();
			trackingService.mockLocation(mapCenter.getLatitude(), mapCenter.getLongitude());
		}else {
            log.error("MockingFailed: tracking service == null");
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		log.debug("MapFragment.onCreateView");

		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_map, container, false);
		assert view != null;

		createMapView(view);


		final GeoPoint initialMapCenter;
		final int initialZoomLevel;

		if (savedInstanceState != null) {
			initialMapCenter = new GeoPoint(savedInstanceState.getInt("map-center-lat"),
					savedInstanceState.getInt("map-center-lon"));
			initialZoomLevel = savedInstanceState.getInt("zoom");
		} else if (getArguments() != null) {
			Point centerPoint = getArguments().getParcelable(ARG_POINT);
			initialMapCenter = new GeoPoint(centerPoint.getLatE6(), centerPoint.getLonE6());
			initialZoomLevel = 17;
		} else {
			initialZoomLevel = pref.getInt(PREF_ZOOM, 15);
			initialMapCenter = new GeoPoint(pref.getFloat(PREF_LATITUDE, 61.7833f),
					pref.getFloat(PREF_LONGITUDE, 34.35f));
		}

		//createClickHandlerOverlay();
		createCenterOverlay();
		updatePointsOverlay();
		createMyPositionOverlay();

		final ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mapView.getController().setZoom(initialZoomLevel);
				mapView.getController().setCenter(initialMapCenter);
			}
		});




		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		log.trace("MapFragment onStart");

		getActivity().bindService(new Intent(getActivity(), TrackingService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Location location = intent.getParcelableExtra(TrackingService.ARG_LOCATION);
				if (myPositionOverlay != null) {
					myPositionOverlay.setLocation(location);
					mapView.invalidate();
				}
			}
		}, new IntentFilter(TrackingService.BC_ACTION_NEW_LOCATION));

        /*
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(pointInRangeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final Point point = intent.getParcelableExtra(TrackingService.ARG_POINT);

				PointDetailFragment detailsFragment = (PointDetailFragment) getFragmentManager().findFragmentByTag("details-fragment");
				if (detailsFragment != null) {
					getFragmentManager().popBackStack("details-fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}

				detailsFragment = PointDetailFragment.newInstance(point, true);
				getFragmentManager().beginTransaction()
						.addToBackStack("details-fragment")
						.add(R.id.panel_details, detailsFragment, "details-fragment")
						.commit();
			}
		}, new IntentFilter(TrackingService.BC_ACTION_POINT_IN_RANGE)); */
	}

	@Override
	public void onResume() {
		super.onResume();

		/*if (getArguments() != null) {
			Point point = getArguments().getParcelable(ARG_POINT);
			centerOn(new GeoPoint(point.getLatE6(), point.getLonE6()), 17);
		}*/
	}

	@Override
	public void onStop() {
		pref.edit()
				.putInt(PREF_ZOOM, mapView.getZoomLevel())
				.putFloat(PREF_LATITUDE, (float) mapView.getMapCenter().getLatitude())
				.putFloat(PREF_LONGITUDE, (float) mapView.getMapCenter().getLongitude()).apply();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);
		//LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pointInRangeReceiver);

		getActivity().unbindService(serviceConnection);

		super.onStop();
	}

	@Override
	public void onDestroy() {
		log.trace("MapFragment onDestroy");
		super.onDestroy();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pref.unregisterOnSharedPreferenceChangeListener(this);

		for (EditOverlay trackOverlay : trackOverlays) {
			trackOverlay.close();
		}

		mapView.getTileProvider().clearTileCache();

		trackManager = null;
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			multiPanel = (MultiPanel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MultiPanel");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		log.debug("MapFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		IGeoPoint screenPos = mapView.getMapCenter();
		int zoom = mapView.getZoomLevel();

		outState.putInt("map-center-lat", screenPos.getLatitudeE6());
		outState.putInt("map-center-lon", screenPos.getLongitudeE6());
		outState.putInt("zoom", zoom);
	}

	private void createMapView(View view) {
		final Context context = getActivity();
		final ViewGroup layout = (ViewGroup) view.findViewById(R.id.map_layout);
		final ResourceProxyImpl proxy = new ResourceProxyImpl(this.getActivity().getApplicationContext());

		/*final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(context.getApplicationContext());


		final OnlineTileSourceBase tileSource = TileSourceFactory.MAPQUESTOSM;

		final TileWriter tileWriter = new TileWriter();
		final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(registerReceiver, tileSource);

		final NetworkAvailabliltyCheck networkAvailabilityCheck = new NetworkAvailabliltyCheck(context);
		final MapTileDownloader downloaderProvider = new MapTileDownloader(tileSource, tileWriter, networkAvailabilityCheck);

		final MapTileProviderArray tileProviderArray = new MapTileProviderArray(tileSource, registerReceiver,
				new MapTileModuleProviderBase[] { fileSystemProvider, downloaderProvider });
*/


		mapView = new MapView(context, 256, proxy);
		mapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mapView.setMultiTouchControls(true);

		layout.addView(mapView);

		setHardwareAccelerationOff();
	}

	private void createCenterOverlay() {
		Overlay overlay = new Overlay(getActivity()) {
			Paint paint = new Paint();
			{
				paint.setColor(Color.GRAY);
				paint.setStrokeWidth(2);
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
			}

			@Override
			protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
				if (shadow)
					return;

				Projection proj = mapView.getProjection();
				android.graphics.Point mapCenter = proj.toPixels(mapView.getMapCenter(), null);
				canvas.drawCircle(mapCenter.x, mapCenter.y, 5, paint);
			}
		};

		mapView.getOverlays().add(overlay);
	}

	private void updatePointsOverlay() {
		// Clear all previous data
		for (EditOverlay trackOverlay : trackOverlays) {
			mapView.getOverlays().remove(trackOverlay);
		}

		trackOverlays.clear();

		CursorHolder activePoints;
		CursorHolder relations;

		// active track can be null after cleaning database
		// unlikely on non-debug use
		Track activeTrack = null;
		String activeTrackName = pref.getString(TrackManager.PREF_TRACK_MODE, null);
		if (activeTrackName != null)
			activeTrack = trackManager.getTrackByName(activeTrackName);

		if (activeTrack != null) {
			Toast.makeText(getActivity(), getResources().getString(R.string.stm), Toast.LENGTH_LONG).show();
			activePoints = trackManager.loadPoints(activeTrack);
			relations = trackManager.loadRelations();
			// TODO: there are no need to use relations
		} else {
			Toast.makeText(getActivity(), getResources().getString(R.string.atm), Toast.LENGTH_LONG).show();
			activePoints = trackManager.loadLocalPoints();
            relations = trackManager.loadRelations();
		}

		freePointsOverlay = new EditOverlay(getActivity(),
				activePoints, relations,
				1, mapView);
        freePointsOverlay.setDrawDraggingItem(dragging);
		freePointsOverlay.setListener(trackOverlayListener);
        //trackColors = freePointsOverlay.getTrackColors();
//        log.error("Track colors size = {}", trackColors.size());

		trackOverlays.add(freePointsOverlay);
		mapView.getOverlays().add(freePointsOverlay);

		mapView.invalidate();
	}

	private void createMyPositionOverlay() {
		myPositionOverlay = new MyPositionOverlay(getActivity(), mapView);
		mapView.getOverlays().add(myPositionOverlay);

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		myPositionOverlay.setRange(pref.getInt(SettingsActivity.PREF_RANGE, 50));
	}

    /*
	private void createClickHandlerOverlay() {
		Overlay clickHandlerOverlay = new Overlay(getActivity()) {
			@Override
			protected void draw(Canvas c, MapView mapView, boolean shadow) {
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
				if (bottomToolbar != null) {
					Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_down);
					assert anim != null;
					bottomToolbar.startAnimation(anim);
					bottomToolbar.setVisibility(View.GONE);
					bottomToolbar = null;
				}

				return false;
			}
		};

		mapView.getOverlays().add(clickHandlerOverlay);
	} */

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setHardwareAccelerationOff() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		log.debug("MapFragment onActivityResult {}, {}", requestCode, resultCode);
	}

	public void centerOn(GeoPoint geoPoint, int zoom) {
		if (mapView != null) {
			mapView.getController().setZoom(zoom);
			mapView.getController().animateTo(geoPoint);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(SettingsActivity.PREF_RANGE)) {
			if (myPositionOverlay != null) {
				myPositionOverlay.setRange(sharedPreferences.getInt(s, 50));
			}
		}
	}

    private void showIntroDialog() {

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            if (!pref.getBoolean(SettingsActivity.PREF_INTRO_DISABLED, false)
                    && !introShown) {
                IntroDialog dialog = new IntroDialog() {
                    @Override
                    protected void onAccept() {

                    }
                };
                dialog.setParams(R.string.app_name,
                        R.string.action_settings,
                        SettingsActivity.PREF_INTRO_DISABLED);
                introShown = true;
                dialog.show(getFragmentManager(), "intro-dialog");
            }

    }



	private class TrackingServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			trackingService = ((TrackingService.TrackingServiceBinder) iBinder).getService();
			trackingService.sendLastLocation();
            if(myPositionOverlay!= null && myPositionOverlay.getLocation() == null)
                mockLocation();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			trackingService = null;
		}
	}

	private SelectTrackDialog.Listener addPointToTrackListener = new SelectTrackDialog.Listener() {
		@Override
		public void trackSelected(Track track) {
			InsertPointDialog dialog = InsertPointDialog.newInstance(track, selectedPoint);
			dialog.show(getFragmentManager(), "insert-point-dialog");
		}
	};

	private SelectTrackDialog.Listener activateTrackListener = new SelectTrackDialog.Listener() {
		@Override
		public void trackSelected(Track track) {
			trackManager.activateTrackMode(track);
			updatePointsOverlay();
		}
	};

	private EditPointDialog.Listener editDialogListener = new EditPointDialog.Listener() {
		@Override
		public void pointCreated(Point point, String track) {
			log.debug("Point created callback, track = {}", track);

			IGeoPoint mapCenter = mapView.getMapCenter();
			point.setCoordinates(mapCenter.getLatitudeE6(), mapCenter.getLongitudeE6());

			point.setPrivate(true);
			trackManager.insertPoint(point);
            if(!track.equalsIgnoreCase(getResources().getString(R.string.pd_no_track))){
                Track t = trackManager.getTrackByHName(track);
                if(t!= null) {
                    trackManager.insertToTrack(t, point);
                }else{
                    log.error("t = null in map fr");
                }
            }
		}

		@Override
		public void pointUpdated(Point point) {
			log.debug("Point updated callback");
			trackManager.insertPoint(point);
		}
	};

    private SelectTrackDialog.NewTrackListener showTrackDialogListener = new SelectTrackDialog.NewTrackListener() {
        @Override
        public void showEditTrackDialog(){
            EditTrackDialog dialog = EditTrackDialog.newInstance(null);
            dialog.setListener(editTrackDialogListener);
            dialog.show(getFragmentManager(), "edit-track-dialog");
        }
    };

    private EditTrackDialog.Listener editTrackDialogListener = new EditTrackDialog.Listener() {
        @Override
        public void trackCreated(Track track) {
            log.debug("Track created callback");
            track.setLocal(true);
            track.setPrivate(true);
            trackManager.insertTrack(track);
            //trackManager.storeLocal(track);
        }

        @Override
        public void trackUpdated(Track track) {
            log.debug("Track updated callback");
            //trackManager.storeLocal(track);
        }
    };

    private PointDetailFragment.Listener pointChangesListener = new PointDetailFragment.Listener() {
        @Override
        public void editPoint() {
            if(!selectedPoint.isEditable()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.point_not_editable), Toast.LENGTH_SHORT).show();
                return;
            }
            EditPointDialog editPointDialog = EditPointDialog.newInstance(selectedPoint);
            editPointDialog.setListener(editDialogListener);
            editPointDialog.show(getFragmentManager(), "edit-point-dialog");
        }

        @Override
        public void addToTrack() {
            SelectTrackDialog dialog = SelectTrackDialog.newInstance();
            dialog.setListener(addPointToTrackListener);
            dialog.setNewTrackListener(showTrackDialogListener);
            if(trackColors!= null)
                dialog.setColors(trackColors);
            dialog.show(getFragmentManager(), "select-track-dialog");
        }

        @Override
        public void drag(){
            if(!selectedPoint.isEditable()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.point_not_editable), Toast.LENGTH_SHORT).show();
                return;
            }
            ActionBarActivity activity = (ActionBarActivity) getActivity();
			activity.startSupportActionMode(pointActionMode);
            dragStart = new GeoPoint(selectedPoint.getLatE6(), selectedPoint.getLonE6());
            dragging = true;
            freePointsOverlay.setDrawDraggingItem(dragging);
            //updatePointsOverlay();

        }

       };

    private void stopDrag(){
        dragging = false;
        freePointsOverlay.setDrawDraggingItem(false);
    }
	private EditOverlay.Listener trackOverlayListener = new EditOverlay.Listener() {
		@Override
		public void pointMoved(Point point, IGeoPoint geoPoint) {
            dragCoords = geoPoint;
			//point.setCoordinates(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());
			//trackManager.insertPoint(point);
		}

		@Override
		public void pointPressed(Point point) {
            if(dragging)
                return;
            selectedPoint = point;
			PointDetailFragment detailsFragment = PointDetailFragment.newInstance(point, true);
            detailsFragment.setListener(pointChangesListener);
			getActivity().getSupportFragmentManager().beginTransaction()
					.addToBackStack("details-fragment")
					.replace(R.id.panel_details, detailsFragment, "details-fragment")
					.commit();
		}

		@Override
		public void pointLongPressed(Point point) {
			selectedPoint = point;
			/*ActionBarActivity activity = (ActionBarActivity) getActivity();
			activity.startSupportActionMode(pointActionMode);*/
		}
	};

	private ActionMode.Callback pointActionMode = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
			actionMode.getMenuInflater().inflate(R.menu.point_drag_menu, menu);
            mapView.invalidate();
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
			switch (menuItem.getItemId()) {
			case R.id.action_apply_drag:
                selectedPoint.setCoordinates(dragCoords.getLatitudeE6(), dragCoords.getLongitudeE6());
                trackManager.insertPoint(selectedPoint);
				actionMode.finish();
				return true;

			case R.id.action_cancel_drag:
                selectedPoint.setCoordinates(dragStart.getLatitudeE6(), dragStart.getLongitudeE6());
                trackManager.insertPoint(selectedPoint);
				actionMode.finish();
				return true;

			default:
                selectedPoint.setCoordinates(dragCoords.getLatitudeE6(), dragCoords.getLongitudeE6());
                trackManager.insertPoint(selectedPoint);
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode actionMode) {
            stopDrag();
            mapView.invalidate();
		}
	};
}
