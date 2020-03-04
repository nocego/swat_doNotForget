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
import java.util.TimeZone;

public class TodoItemHelper {

    private long calendarId;

    public boolean askPermissionWriteCalendar(AppCompatActivity activity){
        int permissionRequestCode = 1;

        if(activity.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.WRITE_CALENDAR};

            activity.requestPermissions(permissions, permissionRequestCode);
        }else{
            return true;
        }
        return false;
    }

    public boolean askPermissionReadCalendar(AppCompatActivity activity){
        int permissionRequestCode = 2;

        if(activity.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED){
            String[] permissions = {Manifest.permission.READ_CALENDAR};

            activity.requestPermissions(permissions, permissionRequestCode);
        }else{
            return true;
        }
        return false;
    }

    public long writeToCal(AppCompatActivity activity, String dateInString, String title, String location, String attendee) {
        long startDate = getStartDate(dateInString);
        long endDate = getEndDate(startDate, 1);

        //set calendarId if none is set
        if(calendarId == 0){
            setCalendarId(activity);
        }

        ContentValues event = new ContentValues();
        event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        event.put(CalendarContract.Events.TITLE, title);
        event.put(CalendarContract.Events.EVENT_LOCATION, location);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        event.put(CalendarContract.Events.DTSTART, startDate);
        event.put(CalendarContract.Events.DTEND, endDate);

        event.put(CalendarContract.Events.ALL_DAY, 0); // 0 for false, 1 for true
        event.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
        event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true

        String eventUriString = "content://com.android.calendar/events";
        Uri eventUri = activity.getApplicationContext()
                .getContentResolver()
                .insert(Uri.parse(eventUriString), event);
        long eventId = Long.parseLong(eventUri.getLastPathSegment());

        //add attendee
        if(attendee != null){
            addAttendee(activity, eventId, attendee);
        }
        return eventId;
    }

    public long getStartDate(String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        //convert String to Date-Object
        Date startDate = null;
        try{
            startDate = formatter.parse(dateInString);
        }catch (Exception ex){
            Log.e("Exception", ex.getMessage());
        }
        return startDate.getTime();
    }

    public long getEndDate(long startDate, int durationInHours){
        return startDate + durationInHours*60*60*1000;
    }

    public void addAttendee(AppCompatActivity activity, long eventId, String name){
        ContentResolver cr = activity.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        values.put(CalendarContract.Attendees.EVENT_ID, eventId);
        cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
    }

    private void setCalendarId(AppCompatActivity activity){
        final String[] eventProjection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        final ContentResolver cr = activity.getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cur = cr.query(uri, eventProjection, null, null, null);

        while (cur.moveToNext()) {
            String name = cur.getString(1);
            if(name.indexOf('@')!=-1){
                calendarId = cur.getLong(0);
            }
        }
        cur.close();
    }
}
