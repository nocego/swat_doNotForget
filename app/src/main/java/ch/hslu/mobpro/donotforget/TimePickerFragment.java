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
    public void onAttach(Context context) {
        super.onAttach(context);

        timeListener = (TimeListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if(getArguments() != null) {
            hour = getArguments().getInt("hour");
            minute = getArguments().getInt("minute");
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (timeListener != null) {
            timeListener.onTimeSet(view, hourOfDay, minute);
        }
    }
}
