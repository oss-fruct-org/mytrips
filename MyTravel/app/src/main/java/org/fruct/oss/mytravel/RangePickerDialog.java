package org.fruct.oss.mytravel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fruct.oss.audioguide.App;
import org.fruct.oss.audioguide.R;
import org.fruct.oss.audioguide.fragments.PointDetailFragment;
import org.fruct.oss.audioguide.util.Utils;

public class RangePickerDialog extends DialogFragment implements DialogInterface.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Listener listener;
    public interface Listener{
        public void valueSelected(int value);
    }
    public void setListener(RangePickerDialog.Listener l){
        this.listener = l;
    }
    private static class Converter {
        int toSliderPosition(int value) {
            return value;
        }

        int toValue(int sliderPosition) {
            return sliderPosition;
        }
    }

    private static class ExponentialConverter extends Converter {
        private double ln10 = Math.log(10);

        @Override
        int toSliderPosition(int value) {
            return (int) (1000 * Math.log(value - 500) / ln10);
        }

        @Override
        int toValue(int sliderPosition) {
            return (int) (Math.exp(sliderPosition* ln10 / 1000) + 500);
        }
    }

    private int showHelpId; // @String: "Show help"
    private int disableId; // @String: "Dont show again"
    private String pref; // preference name

    private int min;
    private int max;
    private int current;
    private int plural;

    private TextView text;
    private SeekBar seekBar;

    private Converter converter;

    public void setParams(int plural_form, int min, int max, int current){
        this.min = min;
        this.max = max;
        this.plural = plural_form;
        this.current = current;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), Utils.getDialogTheme()));

        // builder.setMessage(messageId);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        View v = LayoutInflater.from(App.getContext()).inflate(R.layout.dialog_range_picker, null);

        text = (TextView) v.findViewById(R.id.rangedialog_text);

        seekBar = (SeekBar) v.findViewById(R.id.range_seekBar);
        converter = new Converter();

        seekBar.setMax(converter.toSliderPosition(max));
        seekBar.setProgress(converter.toSliderPosition(current));
        int value = converter.toSliderPosition(current);
        text.setText(App.getContext().getResources().getQuantityString(plural, value, value));
        seekBar.setOnSeekBarChangeListener(this);
        builder.setView(v);

        return builder.create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progressBarPosition, boolean fromUser) {
        int value = converter.toValue(progressBarPosition);

        text.setText(App.getContext().getResources().getQuantityString(plural, value, value));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        listener.valueSelected(converter.toValue(seekBar.getProgress()));
    }
}
