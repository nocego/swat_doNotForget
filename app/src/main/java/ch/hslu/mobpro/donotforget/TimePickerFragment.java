package ch.hslu.mobpro.donotforget;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

private TimeListener timeListener;

public interface TimeListener {
    void onTimeSet(TimePicker view, int hourOfDay, int minute);
}

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        timeListener = (TimeListener) context;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {//NOPMD
        final int hour = getHour();
        final int minute = getMinute();

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        if (timeListener != null) {
            timeListener.onTimeSet(view, hourOfDay, minute);
        }
    }

    private int getHour(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : getArguments().getInt("hour");
    }

    private int getMinute(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.MINUTE) : getArguments().getInt("minute");
    }
}
