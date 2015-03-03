package org.fruct.oss.audioguide.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.android.internal.util.Predicate;

import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.dialogs.WebViewDialog;
import org.fruct.oss.audioguide.gets.Gets;
import org.fruct.oss.audioguide.gets.LoginStage1Request;
import org.fruct.oss.audioguide.parsers.AuthRedirectResponse;
import org.fruct.oss.audioguide.parsers.GetsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static Logger log = LoggerFactory.getLogger(SettingsActivity.class);
	public static final String PREF_RANGE = "pref_range";
	public static final String PREF_WAKE = "pref_wake";
	public static final String PREF_LOAD_RADIUS = "pref_load_radius";
    public static final String PREF_INTRO_DISABLED = "pref_show_intro";

	private SliderPreference rangePreference;
	private SliderPreference loadRadiusPreference;
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		rangePreference = (SliderPreference) findPreference(PREF_RANGE);

		loadRadiusPreference = ((SliderPreference) findPreference(PREF_LOAD_RADIUS));
        log.error("In settings activity");
	}

	@Override
	protected void onResume() {
		super.onResume();

		assert getPreferenceScreen() != null;
		pref = getPreferenceScreen().getSharedPreferences();

		updateRangeSummary();
		updateLoadRadiusSummary();

		pref.registerOnSharedPreferenceChangeListener(this);
        log.error("In settings activity");
	}

	@Override
	protected void onPause() {
		pref.unregisterOnSharedPreferenceChangeListener(this);

		super.onPause();
	}

	private void updateRangeSummary() {
		int value = pref.getInt(PREF_RANGE, 50);
		rangePreference.setSummary(getResources().getQuantityString(R.plurals.pref_seek_bar_summary,
				value, value));
	}

	private void updateLoadRadiusSummary() {
		int value = pref.getInt(PREF_LOAD_RADIUS, 1000);
		loadRadiusPreference.setSummary(getResources().getQuantityString(R.plurals.pref_load_radius_summary,
				value, value));
	}



	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(PREF_RANGE)) {
			updateRangeSummary();
		} else if (s.equals(PREF_LOAD_RADIUS)) {
			updateLoadRadiusSummary();
		}
	}
}
