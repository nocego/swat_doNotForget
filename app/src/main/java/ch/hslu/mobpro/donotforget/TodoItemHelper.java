package ch.hslu.mobpro.donotforget;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TodoItemHelper {

    private long calendarId;

    public boolean askPermissionWriteCalendar(final AppCompatActivity activity){
        int permissionRequestCode = 1;//NOPMD

        if(activity.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED){
            final String[] permissions = {Manifest.permission.WRITE_CALENDAR};

            activity.requestPermissions(permissions, permissionRequestCode);
        }else{
            return true;
        }
        return false;
    }

    public boolean askPermissionReadCalendar(final AppCompatActivity activity){
        int permissionRequestCode = 2;//NOPMD

        if(activity.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED){
            final String[] permissions = {Manifest.permission.READ_CALENDAR};

            activity.requestPermissions(permissions, permissionRequestCode);
        }else{
            return true;
        }
        return false;
    }

    public long writeToCal(final AppCompatActivity activity, final String dateInString, final String title, final String location, final String attendee) {
        final long startDate = getStartDate(dateInString);
        final long endDate = getEndDate(startDate, 1);

        //set calendarId if none is set
        if(calendarId == 0){
            setCalendarId(activity);
        }

        final ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.EVENT_LOCATION, location);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        event.put(CalendarContract.Events.DTSTART, startDate);
        event.put(CalendarContract.Events.DTEND, endDate);

        event.put(CalendarContract.Events.ALL_DAY, 0); // 0 for false, 1 for true
        event.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true

        final String eventUriString = "content://com.android.calendar/events";
        final Uri eventUri = activity.getApplicationContext()
                .getContentResolver()
                .insert(Uri.parse(eventUriString), event);
        final long eventId = Long.parseLong(eventUri.getLastPathSegment());

        //add attendee
        if(attendee != null){
            addAttendee(activity, eventId, attendee);
        }
        return eventId;
    }

    public long getStartDate(final String dateInString){
        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

        //convert String to Date-Object
        Date startDate = null;
        try{
            startDate = formatter.parse(dateInString);
        }catch (Exception ex){
            Log.e("Exception", ex.getMessage());
        }
        return startDate.getTime();
    }

    public long getEndDate(final long startDate, final int durationInHours){
        return startDate + durationInHours*60*60*1000;
    }

    public void addAttendee(final AppCompatActivity activity, final long eventId, final String name){
        final ContentResolver contRes = activity.getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        values.put(CalendarContract.Attendees.EVENT_ID, eventId);
        contRes.insert(CalendarContract.Attendees.CONTENT_URI, values);
    }

    private void setCalendarId(final AppCompatActivity activity){
        final String[] eventProjection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        final ContentResolver contRes = activity.getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final Cursor cur = contRes.query(uri, eventProjection, null, null, null);

        while (cur.moveToNext()) {
            final String name = cur.getString(1);
            if(name.indexOf('@')!=-1){
                calendarId = cur.getLong(0);
            }
        }
        cur.close();
    }
}
