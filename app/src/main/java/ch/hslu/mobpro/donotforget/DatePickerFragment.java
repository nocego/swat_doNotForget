package ch.hslu.mobpro.donotforget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener{

    private DateListener dateListener;

    public interface DateListener {
        void onDateSet(DatePicker view, int year, int month, int day);
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        dateListener = (DateListener) context;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
        if (dateListener != null) {
            dateListener.onDateSet(view, year, month, day);
        }
    }
}
