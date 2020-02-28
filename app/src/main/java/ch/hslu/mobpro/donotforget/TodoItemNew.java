package ch.hslu.mobpro.donotforget;

import android.Manifest;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodosDatabase;

public class TodoItemNew extends AppCompatActivity implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener{

    private TodoItemDao todoItemDao;
    private TodoItemsDatabase todoItemsDb;
    private int currentTodoId;
    private final Calendar calendar = Calendar.getInstance();
    private boolean canWriteToCalendar = false;
    private long calendarEventId = -1;
    private long calendarId = 0;
    private CheckBox calendarCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_new);
        calendarCheckbox = findViewById(R.id.checkBox);

        renameActionBar("Neues To-Do Item");
        addBackButtonToActionBar();
        createTodoItemsDb();

        getTodoIdFromIntent();
        askPermissionReadCalendar();
        askPermissionWriteCalendar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            openTodoDetail();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTodoDetail(View v){
        openTodoDetail();
    }

    private void openTodoDetail(){
        Intent intent = new Intent(this, TodoDetail.class);
        intent.putExtra("todoId", currentTodoId);
        startActivity(intent);
    }

    public void saveNewTodoItem(View v){
        EditText title = findViewById(R.id.editText5);
        Button date = findViewById(R.id.dateButton);
        Button time = findViewById(R.id.timeButton);
        EditText place = findViewById(R.id.editText6);
        EditText staff = findViewById(R.id.editText8);
        CheckBox inCalendar = findViewById(R.id.checkBox);

        CharSequence dateFinal = "01.01.1970";
        if(!date.getText().equals("Datum")){
            dateFinal = date.getText();
        }

        CharSequence timeFinal = "00:00";
        if(!time.getText().equals("Zeit")){
            timeFinal = time.getText();
        }
        TodoItem todoitem = new TodoItem();
        todoitem.todoId=currentTodoId;
        todoitem.title=title.getText().toString();
        todoitem.date=dateFinal+" "+timeFinal;
        todoitem.place=place.getText().toString();
        todoitem.staff=staff.getText().toString();
        todoitem.inCalendar=inCalendar.isChecked();
        if(canWriteToCalendar == false){
            todoitem.inCalendar=false;
        }


        if(inCalendar.isChecked()){
            if(canWriteToCalendar){
                this.calendarEventId = writeToCal(todoitem.date, todoitem.title, todoitem.place, todoitem.staff);
            }
        }
        todoitem.calendarEventId=this.calendarEventId;

        todoItemDao.insertAllTodoItem(todoitem);
        Toast.makeText(getApplicationContext(), "Ihr ToDo-Item wurde abgespeichert.", Toast.LENGTH_LONG).show();

        openTodoDetail();
    }

    private void renameActionBar(String name){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }

    private void addBackButtonToActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void createTodoItemsDb(){
        todoItemsDb = Room.databaseBuilder(
                getApplicationContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
        todoItemDao = todoItemsDb.todoItemDao();
    }

    private void getTodoIdFromIntent(){
        int todoId = getIntent().getExtras().getInt("todoId");
        currentTodoId = todoId;
    }

    private void updateDateButtonLabel() {
        final String year = String.valueOf(this.calendar.get(Calendar.YEAR));
        final String month = String.format("%02d", this.calendar.get(Calendar.MONTH)+1);
        final String day = String.format("%02d", this.calendar.get(Calendar.DAY_OF_MONTH));

        Button button = findViewById(R.id.dateButton);
        button.setText(day + "." + month + "." + year);
    }

    private void updateTimeButtonLabel() {
        final String hour = String.format("%02d", this.calendar.get(Calendar.HOUR_OF_DAY));
        final String minute = String.format("%02d", this.calendar.get(Calendar.MINUTE));

        Button button = findViewById(R.id.timeButton);
        button.setText(hour + ":" + minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.calendar.set(year, month, day);
        this.updateDateButtonLabel();
    }

    public void dateButtonClicked(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void timeButtonClicked(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(Calendar.MINUTE, minute);
        this.updateTimeButtonLabel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWriteToCalendar = true;
                    Toast.makeText(TodoItemNew.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    canWriteToCalendar = false;
                    Toast.makeText(TodoItemNew.this, "Keine Todos werden in den Kalender gespeichert.", Toast.LENGTH_LONG).show();
                    calendarCheckbox.setEnabled(false);
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    canWriteToCalendar=true;
                    Toast.makeText(TodoItemNew.this, "Permisson granted!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    canWriteToCalendar=false;
                    Toast.makeText(TodoItemNew.this, "Keine Todos werden in den Kalender gespeichert.", Toast.LENGTH_LONG).show();
                    calendarCheckbox.setEnabled(false);
                }
                break;
        }
    }

    private void askPermissionWriteCalendar(){
        int PERMISSION_REQUEST_CODE = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if(checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.WRITE_CALENDAR};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }else{
                canWriteToCalendar = true;
            }
        }
    }

    private void askPermissionReadCalendar(){
        int PERMISSION_REQUEST_CODE = 2;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if(checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.READ_CALENDAR};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }else{
                canWriteToCalendar = true;
            }
        }
    }

    private long writeToCal(String dateInString, String title, String location, String attendee) {
        long startDate = getStartDate(dateInString);
        long endDate = getEndDate(startDate, 1);

        //set calendarId if none is set
        if(calendarId == 0){
            setCalendarId();
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
        Uri eventUri = this.getApplicationContext()
                .getContentResolver()
                .insert(Uri.parse(eventUriString), event);
        long eventId = Long.parseLong(eventUri.getLastPathSegment());

        //add attendee
        if(attendee != null){
            addAttendee(eventId, attendee);
        }
        return eventId;
    }

    private long getStartDate(String dateInString){
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

    private long getEndDate(long startDate, int durationInHours){
        long endDate = startDate + durationInHours*60*60*1000;
        return endDate;
    }

    private void setCalendarId(){
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
        };

        final ContentResolver cr = getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        Cursor cur = cr.query(uri, EVENT_PROJECTION, null, null, null);
        //final List<CalendarInfo> result = Lists.newArrayList();

        while (cur.moveToNext()) {
            String name = cur.getString(1);
            if(name.indexOf('@')!=-1){
                calendarId = cur.getLong(0);
            }
        }
    }

    private void addAttendee(long eventId, String name){
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        values.put(CalendarContract.Attendees.EVENT_ID, eventId);
        Uri uri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
    }
}
