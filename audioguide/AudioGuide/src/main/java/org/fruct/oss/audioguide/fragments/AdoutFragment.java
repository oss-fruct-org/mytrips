package org.fruct.oss.audioguide.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.Predicate;

import org.fruct.oss.audioguide.MultiPanel;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.WebViewDialog;
import org.fruct.oss.audioguide.gets.Gets;
import org.fruct.oss.audioguide.gets.LoginStage1Request;
import org.fruct.oss.audioguide.gets.LoginStage2Request;
import org.fruct.oss.audioguide.parsers.AuthRedirectResponse;
import org.fruct.oss.audioguide.parsers.GetsResponse;
import org.fruct.oss.audioguide.track.GetsStorage;

/**
 * Created by ASUS on 29.05.2014.
 */

public class AdoutFragment extends Fragment implements WebViewDialog.Listener, SharedPreferences.OnSharedPreferenceChangeListener {
    private MultiPanel multiPanel;

    private TextView loginLabel;


    // TODO: store between screen rotations
    private String sessionId;
    private SharedPreferences pref;
    private Button logoutButton;
    private Button signInButton;

    public static AdoutFragment newInstance() {
        return new AdoutFragment();
    }
    public AdoutFragment() {
    }

    private void logout() {
        pref.edit().remove(GetsStorage.PREF_AUTH_TOKEN).apply();
        initializeLoginLabel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
      //  assert view != null;

        //signInButton = (Button) view.findViewById(R.id.sign_in_button);

       // logoutButton = ((Button) view.findViewById(R.id.logout_button));
      //  loginLabel = ((TextView) view.findViewById(R.id.login_label));

       // logoutButton.setOnClickListener(new View.OnClickListener() {
       //     @Override
        //    public void onClick(View view) {
        //        logout();
          //  }
       // });
/*
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



        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);

        initializeLoginLabel();
        return view;*/
      //  loginLabel.setText("О программе\nСтепан");
        return view;
    }

    @Override
    public void onDestroyView() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroyView();
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

                WebViewDialog authDialog = new WebViewDialog(redirect.getRedirectUrl(), new Predicate<String>() {
                    @Override
                    public boolean apply(String url) {
                        return url.startsWith(Gets.GETS_SERVER + "/include/GoogleAuth.php");
                    }
                });
                authDialog.show(getFragmentManager(), "auth-dialog");
                authDialog.setListener(AdoutFragment.this);
            }

            @Override
            protected void onError() {
                showError("Ошибка входа");
            }
        });
    }

    private void authenticateStage2() {
        Gets gets = Gets.getInstance();
        gets.addRequest(new LoginStage2Request(gets, sessionId) {
            @Override
            protected void onError() {
                showError("Ошибка входа");
            }
        });
    }

    @Override
    public void onSuccess() {
        authenticateStage2();
    }

    private void initializeLoginLabel() {
        String token = pref.getString(GetsStorage.PREF_AUTH_TOKEN, null);

        if (token == null) {
            loginLabel.setText("Вход не выполнен");
        } else {
            loginLabel.setText("Вход выполнен");
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(GetsStorage.PREF_AUTH_TOKEN)) {
            initializeLoginLabel();
        }
    }

    private void showError(final String error) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
