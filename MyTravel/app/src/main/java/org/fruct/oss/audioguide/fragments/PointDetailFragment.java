package org.fruct.oss.audioguide.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.fruct.oss.audioguide.MultiPanel;
import org.fruct.oss.audioguide.NavigationDrawerFragment;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.config.Config;
import org.fruct.oss.audioguide.files.DefaultFileManager;
import org.fruct.oss.audioguide.files.FileListener;
import org.fruct.oss.audioguide.files.FileManager;
import org.fruct.oss.audioguide.track.DefaultTrackManager;
import org.fruct.oss.audioguide.track.Point;
import org.fruct.oss.audioguide.track.TrackManager;
import org.fruct.oss.audioguide.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointDetailFragment extends Fragment implements FileListener {
    private static final String ARG_POINT = "point";
	private static final String STATE_POINT = "point";
	private static final String ARG_IS_OVERLAY = "c";
	private static final String STATE_IS_OVERLAY = "overlay";
    private final static Logger log = LoggerFactory.getLogger(PointDetailFragment.class);
	private Point point;
	private boolean isOverlay;

	private MultiPanel multiPanel;
	private DefaultFileManager fileManager;

	private String pendingUrl;
	private ImageView imageView;
	//private Bitmap imageBitmap;

    private TrackManager trackManager;
	private int imageSize;
	private boolean isStateSaved;
	private boolean isImageExpanded;

    private Listener listener;

    public interface Listener{
        void editPoint();
        void addToTrack();
        void drag();
    }

    public void setListener(Listener l){this.listener = l;}

	/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param point Point.
     * @param isOverlay
	 * @return A new instance of fragment PointDetailFragment.
     */
    public static PointDetailFragment newInstance(Point point, boolean isOverlay) {
        PointDetailFragment fragment = new PointDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POINT, point);
		args.putBoolean(ARG_IS_OVERLAY, isOverlay);
        fragment.setArguments(args);
        return fragment;
    }

    public PointDetailFragment() {
    }

	@Override
	public void onStart() {
		super.onStart();
		isStateSaved = false;

        /*
		if (point.hasAudio())
			initializeBottomPanel(); */
	}

    /*
    Init panel for playing audio

	private void initializeBottomPanel() {
		PanelFragment panelFragment = (PanelFragment) getFragmentManager().findFragmentByTag("bottom-panel-fragment");

		if (panelFragment == null) {
			panelFragment = PanelFragment.newInstance(point, -1, null);
			getFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.bottom_up, R.anim.bottom_down)
					.replace(R.id.panel_container,
					panelFragment, "bottom-panel-fragment").commit();
		}

		panelFragment.setFallbackPoint(point);
	}*/

	@Override
	public void onStop() {
		if (!isStateSaved) {
			PanelFragment panelFragment = (PanelFragment) getFragmentManager().findFragmentByTag("bottom-panel-fragment");
			if (panelFragment != null) {
				panelFragment.clearFallbackPoint();
			}
		}

		super.onStop();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            point = getArguments().getParcelable(ARG_POINT);
			isOverlay = getArguments().getBoolean(ARG_IS_OVERLAY);
		}

		if (savedInstanceState != null) {
			point = savedInstanceState.getParcelable(STATE_POINT);
			isOverlay = savedInstanceState.getBoolean(STATE_IS_OVERLAY);
		}

        trackManager = DefaultTrackManager.getInstance();

		fileManager = DefaultFileManager.getInstance();
		fileManager.addWeakListener(this);

		setHasOptionsMenu(true);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_point_detail, container, false);
		assert view != null;

		TextView title = (TextView) view.findViewById(android.R.id.text1);
		title.setText(point.getName());

		TextView description = (TextView) view.findViewById(android.R.id.text2);
		String descriptionString = point.getDescription();

		if (!Utils.isNullOrEmpty(descriptionString)) {
			description.setText(descriptionString);
		} else {
			description.setVisibility(View.GONE);
		}

		imageView = (ImageView) view.findViewById(android.R.id.icon);

		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				imageSize = view.getMeasuredWidth();
				tryUpdateImage(imageSize, imageSize);
			}
		});

		setupOverlayMode(view);

		if (isOverlay) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getActivity().getSupportFragmentManager().popBackStack("details-fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
			});
		}


            ImageButton addButton = (ImageButton) view.findViewById(R.id.add_to_track_imagebutton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    listener.addToTrack();
                }
            });

            ImageButton editButton = (ImageButton) view.findViewById(R.id.edit_point_imagebutton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listener.editPoint();
            }
        });

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_imagebutton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startDeletingPoint(point);
            }
        });

        ImageButton dragPoint = (ImageButton) view.findViewById(R.id.drag_point_imagebutton);
        dragPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                listener.drag();
                closeDialog();
            }
        });


        if(listener == null) {
            addButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            dragPoint.setVisibility(View.GONE);

        }
		return view;
	}

    private void closeDialog(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void startDeletingPoint(final Point point) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_point);
        if (!Config.isEditLocked() && point.isPrivate()) {
            builder.setPositiveButton(R.string.delete_track_server, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    trackManager.deletePoint(point, true);
                    closeDialog();
                }
            });
        }

        builder.setNeutralButton(R.string.delete_track_local, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                trackManager.deletePoint(point, false);
                closeDialog();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.point_details_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_show_on_map) {
			showOnMap();
		}

		return super.onOptionsItemSelected(item);
	}

	private void showOnMap() {
		NavigationDrawerFragment frag =
				(NavigationDrawerFragment)
						getActivity().getSupportFragmentManager()
								.findFragmentById(R.id.navigation_drawer);

		Bundle bundle = new Bundle();
		bundle.putParcelable(MapFragment.ARG_POINT, point);
		frag.selectItem(1, bundle);
	}

	private void setupOverlayMode(View view) {
		if (!isOverlay)
			return;

		Drawable background = getResources().getDrawable(R.drawable.marker_4);
		background.setColorFilter(0xccffffff, PorterDuff.Mode.MULTIPLY);

		view.setBackgroundDrawable(background);
	}

	/*private void setupCenterButton(View view) {
		final Button buttonCenter = (Button) view.findViewById(R.id.button_map);
		buttonCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity activity = (MainActivity) getActivity();

				NavigationDrawerFragment frag =
						(NavigationDrawerFragment)
								activity.getSupportFragmentManager()
										.findFragmentById(R.id.navigation_drawer);

				Bundle params = new Bundle();
				params.putParcelable("point", point);

				frag.selectItem(1, params);
			}
		});
	}

	private void setupAudioButton(View view) {
		final Button buttonPlay = (Button) view.findViewById(R.id.button_play);
		final Button buttonStop = (Button) view.findViewById(R.id.button_stop);

		buttonPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (point == null)
					return;

				Intent intent = new Intent(TrackingService.ACTION_PLAY,
						Uri.parse(point.getAudioUrl()),
						getActivity(), TrackingService.class);
				getActivity().startService(intent);

				buttonPlay.setVisibility(View.GONE);
				buttonStop.setVisibility(View.VISIBLE);
			}
		});

		buttonStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(TrackingService.ACTION_STOP,
						null,
						getActivity(), TrackingService.class);
				getActivity().startService(intent);

				buttonPlay.setVisibility(View.VISIBLE);
				buttonStop.setVisibility(View.GONE);
			}
		});

		if (point.hasAudio()) {
			buttonPlay.setVisibility(View.VISIBLE);
		} else {
			buttonPlay.setVisibility(View.GONE);
		}

		buttonStop.setVisibility(View.GONE);
	}*/

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
    public void onDetach() {
		super.onDetach();
		multiPanel = null;

		if (!isOverlay) {
			fileManager.recycleAllBitmaps("point-detail-fragment");
		} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (imageView.getDrawable() instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
				if (bitmap != null && !bitmap.isRecycled())
					bitmap.recycle();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		pendingUrl = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_POINT, point);
		outState.putBoolean(STATE_IS_OVERLAY, isOverlay);

		isStateSaved = true;
	}

	private void tryUpdateImage(int imageWidth, int imageHeight) {
		if (point.hasPhoto()) {
			String remoteUrl = point.getPhotoUrl();
			imageView.setAdjustViewBounds(true);
			fileManager.requestImageBitmap(remoteUrl, imageWidth, imageHeight, FileManager.ScaleMode.NO_SCALE, new FileManager.ImageViewSetter(imageView), "point-detail-fragment");
			pendingUrl = null;
		} else {
			imageView.setVisibility(View.GONE);
		}
	}

	/*private void expandImage() {

	}

	private void shrinkImage() {

	}*/

	@Override
	public void itemLoaded(String url) {
		if (url.equals(pendingUrl)) {
			tryUpdateImage(imageView.getWidth(), imageView.getHeight());
		}
	}

	@Override
	public void itemDownloadProgress(String url, int current, int max) {
	}

	public Point getPoint() {
		return point;
	}
}
