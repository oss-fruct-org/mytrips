package org.fruct.oss.mytravel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import org.fruct.oss.audioguide.App;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntroDialog extends DialogFragment implements DialogInterface.OnClickListener{
    private static Logger log = LoggerFactory.getLogger(IntroDialog.class);
    private CheckBox checkbox;

    private int showHelpId; // @String: "Show help"
    private int disableId; // @String: "Dont show again"
    private String disablePref; // preference name

    // FIXME:рарапрапрап
    public IntroDialog() {

    }


    public void setParams( int showHelpId, int disableId, String disablePref){
        this.showHelpId = showHelpId;
        this.disableId = disableId;
        this.disablePref = disablePref;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), Utils.getDialogTheme()));

        // builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, this);
       // builder.setNegativeButton(android.R.string.cancel, this);


        View v = LayoutInflater.from(App.getContext()).inflate(R.layout.intro_dialog_layout, null);
        checkbox = (CheckBox)v.findViewById(R.id.checkBox);
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (checkbox.isChecked()) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.edit().putBoolean(disablePref, true).apply();
        }

        if (which == AlertDialog.BUTTON_POSITIVE) {
            onAccept();
        }
    }

    protected void onAccept() {

    }
}
