package ch.hslu.mobpro.donotforget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {//NOPMD
        final int year = getYear();
        final int month = getMonth();
        final int day = getDay();

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
        if (dateListener != null) {
            dateListener.onDateSet(view, year, month, day);
        }
    }

    private int getYear(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.YEAR) : getArguments().getInt("year");
    }

    private int getMonth(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.MONTH) : getArguments().getInt("month")-1;
    }

    private int getDay(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : getArguments().getInt("day");
    }
}
