package org.fruct.oss.audioguide;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HelpActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
			HelpEntry[] helpEntries1 = {
				new HelpEntry(getResources().getString(R.string.refresh), getResources().getString(R.string.refresh1), R.drawable.ic_action_refresh, true),
				new HelpEntry(getResources().getString(R.string.filter), getResources().getString(R.string.filter1), 0, true)
			};

			HelpEntry[] helpEntries2 = {
					new HelpEntry(getResources().getString(R.string.tracks), getResources().getString(R.string.tracks1), R.drawable.ic_action_new, true),
					new HelpEntry(getResources().getString(R.string.refresh), getResources().getString(R.string.refresh2), R.drawable.ic_action_refresh, true),
					new HelpEntry(getResources().getString(R.string.activate), getResources().getString(R.string.activate1), R.drawable.ic_action_location_found, true),
					new HelpEntry(getResources().getString(R.string.save), getResources().getString(R.string.save1), R.drawable.ic_action_save, false),
					new HelpEntry(getResources().getString(R.string.delete), getResources().getString(R.string.delete1), R.drawable.ic_action_discard, true)
			};

			HelpEntry[] helpEntries3 = {
					new HelpEntry(getResources().getString(R.string.modes), getResources().getString(R.string.modes1), 0, true),
					new HelpEntry(getResources().getString(R.string.stm), getResources().getString(R.string.stm1), R.drawable.ic_routing, true),
					new HelpEntry(getResources().getString(R.string.atm), getResources().getString(R.string.atm1), R.drawable.ic_routing, true),
					new HelpEntry(getResources().getString(R.string.search), getResources().getString(R.string.search1), R.drawable.ic_action_search, true),
					new HelpEntry(getResources().getString(R.string.place), getResources().getString(R.string.place1), 0, true),
					new HelpEntry(getResources().getString(R.string.addpoint), getResources().getString(R.string.addpoint1), R.drawable.ic_action_new, true)
			};

			HelpEntry[] helpEntries4 = {
					new HelpEntry(getResources().getString(R.string.pointmenu), getResources().getString(R.string.pointmenu1), 0, true),
					new HelpEntry(getResources().getString(R.string.att), getResources().getString(R.string.att1), R.drawable.ic_action_share, true),
					new HelpEntry(getResources().getString(R.string.edit), getResources().getString(R.string.edit1), R.drawable.ic_action_edit, true),
			};

			switch (position) {
			case 0:
				return PlaceholderFragment.newLayoutInstance(helpEntries1);
			case 1:
				return PlaceholderFragment.newLayoutInstance(helpEntries2);
			case 2:
				return PlaceholderFragment.newLayoutInstance(helpEntries3);
			case 3:
				return PlaceholderFragment.newLayoutInstance(helpEntries4);
			default:
				return PlaceholderFragment.newLayoutInstance(helpEntries1);
			}
		}

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 1:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

    }



	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		private static final String ARG_ENTRIES = "layout_number";

		public static Fragment newLayoutInstance(HelpEntry[] entries) {
			ArrayList<HelpEntry> entriesArrList = new ArrayList<HelpEntry>();
			Collections.addAll(entriesArrList, entries);

			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();

			args.putSerializable(ARG_ENTRIES, entriesArrList);

			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			List<HelpEntry> entires = (List) getArguments().getSerializable(ARG_ENTRIES);

			ScrollView scrollView = new ScrollView(getActivity());
			scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			scrollView.setHorizontalScrollBarEnabled(false);
			scrollView.setVerticalScrollBarEnabled(false);

			LinearLayout linearLayout = new LinearLayout(getActivity());
			linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			linearLayout.setOrientation(LinearLayout.VERTICAL);

			scrollView.addView(linearLayout);

			for (HelpEntry entry : entires) {
				View entryView = inflater.inflate(R.layout.help_template, linearLayout, false);

				TextView titleView = (TextView) entryView.findViewById(R.id.help_template_title);
				TextView textView = (TextView) entryView.findViewById(R.id.help_template_text);
				ImageView iconView = (ImageView) entryView.findViewById(R.id.help_template_icon);

				titleView.setText(entry.title);
				textView.setText(entry.text);

				if (entry.iconRes != 0) {
					iconView.setImageResource(entry.iconRes);
					iconView.setVisibility(View.VISIBLE);

					if (entry.reversed) {
						iconView.setColorFilter(0xff515151, PorterDuff.Mode.SRC_ATOP);
					}
				}


				linearLayout.addView(entryView);
			}

			return scrollView;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public boolean onNavigateUp() {
		finish();
		return true;
	}


	private static class HelpEntry implements Serializable {
		HelpEntry(String title, String text, int iconRes, boolean reversed) {
			this.title = title;
			this.text = text;
			this.iconRes = iconRes;
			this.reversed = reversed;
		}

		String title;
		String text;
		int iconRes;
		boolean reversed;
	}
}
