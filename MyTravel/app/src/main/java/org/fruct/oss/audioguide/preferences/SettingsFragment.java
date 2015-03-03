package org.fruct.oss.audioguide.preferences;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.android.internal.util.Predicate;

import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.dialogs.WebViewDialog;
import org.fruct.oss.audioguide.gets.Gets;
import org.fruct.oss.audioguide.gets.LoginStage1Request;
import org.fruct.oss.audioguide.parsers.AuthRedirectResponse;
import org.fruct.oss.audioguide.parsers.GetsResponse;
import org.fruct.oss.audioguide.track.GetsBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, WebViewDialog.Listener, PreferenceFragment.OnPreferenceStartFragmentCallback {
    private final static Logger log = LoggerFactory.getLogger(SettingsFragment.class);
	private SliderPreference rangePreference;
	private SliderPreference loadRadiusPreference;
	private SharedPreferences pref;
    private Preference getsPref;
    private String sessionId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		rangePreference = (SliderPreference) findPreference(SettingsActivity.PREF_RANGE);
		loadRadiusPreference = ((SliderPreference) findPreference(SettingsActivity.PREF_LOAD_RADIUS));
        getsPref = findPreference("pref_gets");
        getsPref.setTitle((getsPref.getTitle()));
	}

	@Override
	public void onResume() {
		super.onResume();

		assert getPreferenceScreen() != null;
		pref = getPreferenceScreen().getSharedPreferences();

		updateRangeSummary();
		updateLoadRadiusSummary();

		pref.registerOnSharedPreferenceChangeListener(this);
        log.error("In setings fragment!1");
	}

	@Override
	public void onPause() {
		pref.unregisterOnSharedPreferenceChangeListener(this);

		super.onPause();
	}

	private void updateRangeSummary() {
		int value = pref.getInt(SettingsActivity.PREF_RANGE, 50);
		rangePreference.setSummary(getResources().getQuantityString(R.plurals.pref_seek_bar_summary,
				value, value));
	}

	private void updateLoadRadiusSummary() {
		int value = pref.getInt(SettingsActivity.PREF_LOAD_RADIUS, 1000);
		loadRadiusPreference.setSummary(getResources().getQuantityString(R.plurals.pref_load_radius_summary,
				value, value));
	}


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(SettingsActivity.PREF_RANGE)) {
			updateRangeSummary();
		} else if (s.equals(SettingsActivity.PREF_LOAD_RADIUS)) {
			updateLoadRadiusSummary();
		}
	}

    private void logout() {
        pref.edit().remove(GetsBackend.PREF_AUTH_TOKEN).apply();
        initializeLoginLabel();
    }



    private void initializeLoginLabel() {
        String token = pref.getString(GetsBackend.PREF_AUTH_TOKEN, null);

        if (token == null) {

        } else {

        }

    }

    @Override
    public void onSuccess() {

    }

    private void showError(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment, Preference preference) {
        return false;
    }
}
