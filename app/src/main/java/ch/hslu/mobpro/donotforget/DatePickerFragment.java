package ch.hslu.mobpro.donotforget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 *Datepicker which can be used within a class to open as DialogFragment
 */
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

    /**
     * set date as current value in datePicker
     * @param savedInstanceState
     * @return DatePickerDialog
     */
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

    /**
     * @return current year or the set year in arguments
     */
    private int getYear(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.YEAR) : getArguments().getInt("year");
    }

    /**
     * @return current month or the set month in arguments
     */
    private int getMonth(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.MONTH) : getArguments().getInt("month")-1;
    }

    /**
     * @return current day or the set day in arguments
     */
    private int getDay(){
        return getArguments() == null ? Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : getArguments().getInt("day");
    }
}
