package org.fruct.oss.audioguide.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.fruct.oss.audioguide.MultiPanel;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.adapters.PointCursorAdapter;
import org.fruct.oss.audioguide.dialogs.InsertPointDialog;
import org.fruct.oss.audioguide.files.DefaultFileManager;
import org.fruct.oss.audioguide.files.FileManager;
import org.fruct.oss.audioguide.track.CursorHolder;
import org.fruct.oss.audioguide.track.DefaultTrackManager;
import org.fruct.oss.audioguide.track.Point;
import org.fruct.oss.audioguide.track.Track;
import org.fruct.oss.audioguide.track.TrackManager;
import org.fruct.oss.audioguide.track.TrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link org.fruct.oss.audioguide.MultiPanel}
 * interface.
 */
public class PointFragment extends ListFragment implements AdapterView.OnItemLongClickListener{
	private final static Logger log = LoggerFactory.getLogger(PointFragment.class);

	private static final String STATE_TRACK = "track";

	public static final String ARG_TRACK = "arg_point";

	private MultiPanel multiPanel;
	private TrackManager trackManager;
	private FileManager fileManager;

    private ActionMode contextMenu;
	private Track track;
    private Point selectedPoint;
    private int selectedPosition;
	private BroadcastReceiver inReceiver;
	private BroadcastReceiver outReceiver;

	private PointCursorAdapter pointAdapter;
	private CursorHolder cursorHolder;

	private TrackingService trackingService;

	private TrackingServiceConnection serviceConnection = new TrackingServiceConnection();

	public static PointFragment newInstance(Track track) {
		Bundle args = new Bundle(1);
		args.putParcelable(ARG_TRACK, track);
		PointFragment fragment = new PointFragment();
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PointFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		trackManager = DefaultTrackManager.getInstance();
		fileManager = DefaultFileManager.getInstance();

		Bundle arguments = getArguments();
		if (arguments != null) {
			track = arguments.getParcelable(ARG_TRACK);
		}

		if (savedInstanceState != null) {
			track = savedInstanceState.getParcelable(STATE_TRACK);
		}

		pointAdapter = new PointCursorAdapter(getActivity(), false);
		cursorHolder = trackManager.loadPoints(track);
		cursorHolder.attachToAdapter(pointAdapter);



		setListAdapter(pointAdapter);

		setHasOptionsMenu(true);
		setupRangeReceiver();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_list_view, container, false);
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().bindService(new Intent(getActivity(), TrackingService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);

        getListView().setOnItemLongClickListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		cursorHolder.close();
		trackManager = null;

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(inReceiver);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(outReceiver);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.refresh, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			trackManager.requestPointsInTrack(track);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setupRangeReceiver() {
		inReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				pointInRange(TrackingService.getPointFromIntent(intent));
			}
		};
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(inReceiver, new IntentFilter(TrackingService.BC_ACTION_POINT_IN_RANGE));

		outReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				pointOutRange(TrackingService.getPointFromIntent(intent));
			}
		};
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(outReceiver, new IntentFilter(TrackingService.BC_ACTION_POINT_OUT_RANGE));
	}

	private void pointInRange(Point point) {
		if (pointAdapter != null)
			pointAdapter.addHighlightedItem(point);
	}

	private void pointOutRange(Point point) {
		if (pointAdapter != null)
			pointAdapter.removeHighlightedItem(point);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			multiPanel = (MultiPanel) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		multiPanel = null;
		fileManager.recycleAllBitmaps("point-fragment");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Point point = pointAdapter.getPoint(position);
        if(contextMenu != null)
            contextMenu.finish();
		multiPanel.replaceFragment(PointDetailFragment.newInstance(point, false), this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_TRACK, track);
	}

	private class TrackingServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			trackingService = ((TrackingService.TrackingServiceBinder) iBinder).getService();
			List<Point> pointsInRange = trackingService.getPointsInRange();
			pointAdapter.setHighlightedItems(pointsInRange);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			trackingService = null;
		}
	}

    private void showContextualActionBar(int position) {
        selectedPoint = pointAdapter.getPoint(position);
        selectedPosition = position;
        getListView().setItemChecked(position, true);
        ActionBarActivity activity = ((ActionBarActivity) getActivity());
        contextMenu = activity.startSupportActionMode(actionModeCallback);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showContextualActionBar(i);
        return true;
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.point_in_track_menu, menu);

            if (!track.isLocal()) {
                menu.findItem(R.id.action_remove_from_track).setVisible(false);
                menu.findItem(R.id.action_edit_position).setVisible(false);
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_remove_from_track:
                    trackManager.removeFromTrack(track, selectedPoint, selectedPosition);
                    actionMode.finish();
                    return true;

                case R.id.action_edit_position:
                    log.error("Position of item before replacement: {} ", selectedPosition);
                    InsertPointDialog dialog = InsertPointDialog.newInstance(track, selectedPoint, true, selectedPosition);
                    dialog.show(getFragmentManager(), "insert-point-dialog");
                    actionMode.finish();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            getListView().setItemChecked(selectedPosition, false);
        }
    };
}
