package oss.fruct.org.mytravel.my_travel;


import android.annotation.TargetApi;
import android.app.Activity;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams;
public class MapFragment extends ContentFragment {

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        createMapView(view);

        createClickHandlerOverlay();

        createCenterOverlay();


        if (savedInstanceState != null) {
            GeoPoint mapCenter = new GeoPoint(savedInstanceState.getInt("map-center-lat"),
                    savedInstanceState.getInt("map-center-lon"));
            int zoom = savedInstanceState.getInt("zoom");

            mapView.getController().setZoom(zoom);
            mapView.getController().setCenter(mapCenter);
        } else {
            mapView.getController().setZoom(15);
            mapView.getController().setCenter(new GeoPoint(61.783333, 34.35));
        }

        for (Overlay overlay : mapView.getOverlays()) {
            log.debug("OVERLAY: {}", overlay.getClass().getName());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        log.trace("MapFragment onStart");




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
    }

    @Override
    public void onResume() {
        super.onResume();
        log.trace("MapFragment onResume");

        if (getArguments() != null) {
            Point point = getArguments().getParcelable("point");
            centerOn(new GeoPoint(point.getLatE6(), point.getLonE6()), 17);
        }
    }

    @Override
    public void onStop() {
        log.trace("MapFragment onStop");
        super.onStop();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationReceiver);

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

    private void setupBottomPanel() {
        bottomToolbar = (ViewGroup) getView().findViewById(R.id.map_toolbar);

        final Button buttonPlay = (Button) bottomToolbar.findViewById(R.id.button_play);
        final Button buttonDetails = (Button) bottomToolbar.findViewById(R.id.button_details);
        final Button buttonStop = (Button) bottomToolbar.findViewById(R.id.button_stop);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPoint == null)
                    return;

                Intent intent = new Intent(AudioService.ACTION_PLAY,
                        Uri.parse(selectedPoint.getAudioUrl()),
                        getActivity(), AudioService.class);
                getActivity().startService(intent);

                buttonPlay.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AudioService.ACTION_STOP,
                        null,
                        getActivity(), AudioService.class);
                getActivity().startService(intent);

                buttonPlay.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.GONE);
            }
        });

        if (selectedPoint.hasAudio()) {
            buttonPlay.setVisibility(View.VISIBLE);
        } else {
            buttonPlay.setVisibility(View.GONE);
        }

        buttonStop.setVisibility(View.GONE);
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

                MapView.Projection proj = mapView.getProjection();
                android.graphics.Point mapCenter = proj.toMapPixels(mapView.getMapCenter(), null);
                canvas.drawCircle(mapCenter.x, mapCenter.y, 5, paint);
            }
        };

        mapView.getOverlays().add(overlay);
    }


    private void createMyPositionOverlay() {
        myPositionOverlay = new MyPositionOverlay(getActivity(), mapView);
        mapView.getOverlays().add(myPositionOverlay);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        myPositionOverlay.setRange(pref.getInt(SettingsActivity.PREF_RANGE, 50));
    }

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
    }

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




    private EditOverlay.Listener trackOverlayListener = new EditOverlay.Listener() {
        @Override
        public void pointMoved(Point point, IGeoPoint geoPoint) {
            assert false;
        }

        @Override
        public void pointPressed(Point point) {
            log.debug("Simple point pressed");

            PointDetailFragment frag = PointDetailFragment.newInstance(point);
            multiPanel.replaceFragment(frag, MapFragment.this);
        }
    };


}
