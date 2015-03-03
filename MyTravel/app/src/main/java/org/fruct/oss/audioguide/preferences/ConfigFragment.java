package org.fruct.oss.audioguide.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.Predicate;

import org.fruct.oss.audioguide.MultiPanel;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.dialogs.WebViewDialog;
import org.fruct.oss.audioguide.gets.Gets;
import org.fruct.oss.audioguide.gets.LoginStage1Request;
import org.fruct.oss.audioguide.gets.LoginStage2Request;
import org.fruct.oss.audioguide.parsers.AuthRedirectResponse;
import org.fruct.oss.audioguide.parsers.GetsResponse;
import org.fruct.oss.audioguide.track.GetsBackend;
import org.fruct.oss.mytravel.RangePickerDialog;


public class ConfigFragment extends Fragment implements WebViewDialog.Listener, SharedPreferences.OnSharedPreferenceChangeListener, RangePickerDialog.Listener{

    PreferenceManager prefManager;

    private String sessionId;
    private MultiPanel multiPanel;
    private SharedPreferences pref;
    private Button logoutButton;
    //private Button manageFilesButton;
    private Button signInButton;
    TextView rangeText;
    TextView getsStatusText;
    TextView radiusText;
    Button button;
    String currentlyEdited;
    LinearLayout rangeLayout, radiusLayout;
    int range, radius;
    RangePickerDialog rpDialog;

    public static ConfigFragment newInstance(){return new ConfigFragment();}
    public ConfigFragment(){}


    @Override
    public void onSuccess() {
        authenticateStage2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        assert view != null;

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);

        rangeText = (TextView) view.findViewById(R.id.rangeText);
        getsStatusText = (TextView) view.findViewById(R.id.getsStatus);
        radiusText = (TextView) view.findViewById(R.id.radiusText);
        signInButton = (Button) view.findViewById(R.id.button_gets);
        rangeLayout = (LinearLayout) view.findViewById(R.id.rangeLayout);
        radiusLayout = (LinearLayout) view.findViewById(R.id.radiusLayout);
        logoutButton = (Button) view.findViewById(R.id.button_gets_logout);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    authenticate();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        updateTexts();

        rangeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentlyEdited = SettingsActivity.PREF_RANGE;
                openDialog(R.plurals.pref_seek_bar_summary, 10, 200, range);
            }
        });

        radiusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentlyEdited = SettingsActivity.PREF_LOAD_RADIUS;
                openDialog(R.plurals.pref_load_radius_summary, 500, 10000, radius);
            }
        });


        return view;
    }

    private void updateTexts(){

      range = pref.getInt(SettingsActivity.PREF_RANGE,50);
        rangeText.setText(getResources().getQuantityString(R.plurals.pref_seek_bar_summary,range,range));

        radius = pref.getInt(SettingsActivity.PREF_LOAD_RADIUS,1000);
        radiusText.setText(getResources().getQuantityString(R.plurals.pref_load_radius_summary,radius, radius));

        String token = pref.getString(GetsBackend.PREF_AUTH_TOKEN, null);

        if (token == null) {
            getsStatusText.setText(getResources().getString(R.string.gets_not_logged));
            logoutButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            getsStatusText.setText(getResources().getString(R.string.gets_logged));
            signInButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    private void openDialog(int plural, int min, int max, int value ){
        rpDialog = new RangePickerDialog();
        rpDialog.setParams(plural, min, max, value);
        rpDialog.setListener(this);
        rpDialog.show(getFragmentManager(),"range-picker-dialog");
    }

    private void logout() {
        pref.edit().remove(GetsBackend.PREF_AUTH_TOKEN).apply();
        updateTexts();
    }

    private void authenticate() {
        Gets gets = Gets.getInstance();
        gets.addRequest(new LoginStage1Request(gets) {
            @Override
            protected void onPostProcess(GetsResponse response) {
                if (response.getCode() == 1) {
                    return;
                }

                AuthRedirectResponse redirect = (AuthRedirectResponse) response.getContent();
                sessionId = redirect.getSessionId();

                WebViewDialog authDialog = WebViewDialog.newInstance(redirect.getRedirectUrl(), new Predicate<String>() {
                    @Override
                    public boolean apply(String url) {
                        return url.startsWith(Gets.GETS_SERVER + "/include/GoogleAuth.php");
                    }
                });

                authDialog.show(getFragmentManager(), "auth-dialog");
                authDialog.setListener(ConfigFragment.this);
            }

            @Override
            protected void onError() {

            }
        });
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
    public void onDetach() {
        super.onDetach();
        multiPanel = null;
    }

    @Override
    public void onDestroyView() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroyView();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(GetsBackend.PREF_AUTH_TOKEN)) {
            updateTexts();
        }
    }

    private void authenticateStage2() {
        Gets gets = Gets.getInstance();
        gets.addRequest(new LoginStage2Request(gets, sessionId) {
            @Override
            protected void onError() {
                showError("Error login");
            }
        });
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
    public void valueSelected(int value) {
        pref.edit().putInt(currentlyEdited, value).apply();
        updateTexts();
    }
}
