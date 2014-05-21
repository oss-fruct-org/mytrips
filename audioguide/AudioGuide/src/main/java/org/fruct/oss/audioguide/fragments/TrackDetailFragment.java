package org.fruct.oss.audioguide.fragments;



import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.track.Track;
import org.fruct.oss.audioguide.track.TrackManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link TrackDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TrackDetailFragment extends Fragment {
	private final static Logger log = LoggerFactory.getLogger(TrackDetailFragment.class);
	private static final String ARG_TRACK = "track";
	private static final String STATE_TRACK = "track";

	private Track track;
	private TrackManager trackManager;

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param track Track to show.
	 * @return A new instance of fragment TrackDetailFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TrackDetailFragment newInstance(Track track) {
		TrackDetailFragment fragment = new TrackDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_TRACK, track);
		fragment.setArguments(args);
		return fragment;
	}

	public TrackDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			track = getArguments().getParcelable(ARG_TRACK);

		if (savedInstanceState != null)
			track = savedInstanceState.getParcelable(STATE_TRACK);

		trackManager = TrackManager.getInstance();
	}

	@Override
	public void onDestroy() {
		trackManager = null;
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_track_detail, container, false);

		final TextView text = (TextView) view.findViewById(android.R.id.text1);
		text.setText(track.getHumanReadableName());

		final TextView desc = (TextView) view.findViewById(android.R.id.text2);
		desc.setText(track.getDescription());

		final Button downloadButton = (Button) view.findViewById(R.id.localImage);
		downloadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				trackManager.storeLocal(track);
				trackManager.refreshPoints(track);
			}
		});

		final Button activateButton = (Button) view.findViewById(R.id.activeImage);
		activateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (track.isActive())
					trackManager.deactivateTrack(track);
				else
					trackManager.activateTrack(track);
				setActivateButtonText(activateButton);
			}
		});

		setActivateButtonText(activateButton);

		return view;
	}

	private void setActivateButtonText(Button button) {
		if (track.isActive())
			button.setText("Deactivate");
		else
			button.setText("Activate");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(STATE_TRACK, track);
	}

    /**
     * A simple {@link android.support.v4.app.Fragment} subclass.
     * Activities that contain this fragment must implement the
     * {@link org.fruct.oss.audioguide.fragments.TrackDetailFragment.PointSerchFragment.OnFragmentInteractionListener} interface
     * to handle interaction events.
     * Use the {@link org.fruct.oss.audioguide.fragments.TrackDetailFragment.PointSerchFragment#newInstance} factory method to
     * create an instance of this fragment.
     *
     */
    public static class PointSerchFragment extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        private OnFragmentInteractionListener mListener;

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PointSerchFragment.
         */
        // TODO: Rename and change types and number of parameters
        public static PointSerchFragment newInstance(String param1, String param2) {
            PointSerchFragment fragment = new PointSerchFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }
        public PointSerchFragment() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_point_serch, container, false);
        }

        // TODO: Rename method, update argument and hook method into UI event
        public void onButtonPressed(Uri uri) {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            try {
                mListener = (OnFragmentInteractionListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mListener = null;
        }

        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */
        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            public void onFragmentInteraction(Uri uri);
        }

    }
}
