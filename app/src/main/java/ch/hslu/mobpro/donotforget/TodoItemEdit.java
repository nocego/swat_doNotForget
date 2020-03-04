package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDatabase;

public class TodoItemEdit extends AppCompatActivity implements DatePickerFragment.DateListener, TimePickerFragment.TimeListener{

    private TodoItemsDatabase todoItemsDb;
    private boolean canWriteToCalendar = false;
    private TodoItemDao todoItemDao;
    private TodoItem currentTodoItem;
    private EditText title;
    private Button date;
    private Button time;
    private EditText place;
    private EditText staff;
    private CheckBox inCalendar;
    private String[] splittedDateTime;
    private final Calendar calendar = Calendar.getInstance();
    private CheckBox calendarCheckbox;
    private TodoItemHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_edit);
        calendarCheckbox = findViewById(R.id.checkBox2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("To-Do-Item bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getDb();
        Intent intent = getIntent();
        getCurrentTodoItem(intent);
        fillActivity();

        helper = new TodoItemHelper();
        canWriteToCalendar = helper.askPermissionReadCalendar(this);
        canWriteToCalendar = helper.askPermissionWriteCalendar(this);
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
        intent.putExtra("todoId", currentTodoItem.todoId);
        startActivity(intent);
    }

    private void getDb(){
        todoItemsDb = Room.databaseBuilder(
                this,
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
    }

    private void getCurrentTodoItem(Intent intent){
        int todoItemId = intent.getExtras().getInt("todoItemId");
        todoItemDao = todoItemsDb.todoItemDao();
        currentTodoItem = todoItemDao.findById(todoItemId);
    }

    private void fillActivity(){
        title = findViewById(R.id.editText9);
        date = findViewById(R.id.dateButton2);
        time = findViewById(R.id.timeButton2);
        place = findViewById(R.id.editText12);
        staff = findViewById(R.id.editText13);
        inCalendar = findViewById(R.id.checkBox2);

        splittedDateTime = currentTodoItem.date.split(" ");

        title.setText(currentTodoItem.title);
        date.setText(splittedDateTime[0]);
        time.setText(splittedDateTime[1]);
        place.setText(currentTodoItem.place);
        staff.setText(currentTodoItem.staff);
        inCalendar.setChecked(currentTodoItem.inCalendar);
    }

    public void udpateTodo(View v){
        currentTodoItem.title = title.getText().toString();
        currentTodoItem.date=date.getText().toString()+" "+time.getText().toString();
        currentTodoItem.place=place.getText().toString();
        currentTodoItem.staff=staff.getText().toString();

        if(canWriteToCalendar) {
            if (currentTodoItem.inCalendar) {
                if (inCalendar.isChecked()) {
                    updateCalendarEntry(currentTodoItem.calendarEventId, currentTodoItem.title, currentTodoItem.place, currentTodoItem.date, currentTodoItem.staff);
                } else {
                    deleteCalendarEntry(currentTodoItem.calendarEventId);
                }
            } else {
                if (inCalendar.isChecked()) {
                    currentTodoItem.calendarEventId = helper.writeToCal(this, currentTodoItem.date, currentTodoItem.title, currentTodoItem.place, currentTodoItem.staff);
                }
            }
        }
        currentTodoItem.inCalendar=inCalendar.isChecked();

        if(!canWriteToCalendar){
            currentTodoItem.inCalendar=false;
        }

        todoItemDao.updateTodos(currentTodoItem);
        Toast.makeText(getApplicationContext(), "Ihr To-Do wurde aktualisiert.", Toast.LENGTH_LONG).show();
        openTodoDetail();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.calendar.set(year, month, day);
        this.updateDateButtonLabel();
    }

    public void dateButtonClicked(View v) {
        String[] splittedDate = (splittedDateTime[0]).split("\\.");

        Bundle arguments = new Bundle();
        arguments.putSerializable("day", Integer.parseInt(splittedDate[0]));
        arguments.putSerializable("month", Integer.parseInt(splittedDate[1]));
        arguments.putSerializable("year", Integer.parseInt(splittedDate[2]));

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(arguments);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void updateDateButtonLabel() {
        final String year = String.valueOf(this.calendar.get(Calendar.YEAR));
        final String month = String.format("%02d", this.calendar.get(Calendar.MONTH)+1);
        final String day = String.format("%02d", this.calendar.get(Calendar.DAY_OF_MONTH));

        Button button = findViewById(R.id.dateButton2);
        String dateCorrect = day + "." + month + "." + year;
        button.setText(dateCorrect);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(Calendar.MINUTE, minute);
        this.updateTimeButtonLabel();
    }

    public void timeButtonClicked(View v) {
        String[] splittedTime = (splittedDateTime[1]).split("\\:");

        Bundle arguments = new Bundle();
        arguments.putSerializable("hour", Integer.parseInt(splittedTime[0]));
        arguments.putSerializable("minute", Integer.parseInt(splittedTime[1]));

        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(arguments);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void updateTimeButtonLabel() {
        final String hour = String.format("%02d", this.calendar.get(Calendar.HOUR_OF_DAY));
        final String minute = String.format("%02d", this.calendar.get(Calendar.MINUTE));

        Button button = findViewById(R.id.timeButton2);
        String timeCorrect = hour + ":" + minute;
        button.setText(timeCorrect);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWriteToCalendar = true;
                    Toast.makeText(TodoItemEdit.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    canWriteToCalendar = false;
                    Toast.makeText(TodoItemEdit.this, "Keine Todos werden in den Kalender gespeichert.", Toast.LENGTH_LONG).show();
                    calendarCheckbox.setEnabled(false);
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    canWriteToCalendar=true;
                    Toast.makeText(TodoItemEdit.this, "Permisson granted!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    canWriteToCalendar=false;
                    Toast.makeText(TodoItemEdit.this, "Keine Todos werden in den Kalender gespeichert.", Toast.LENGTH_LONG).show();
                    calendarCheckbox.setEnabled(false);
                }
                break;
            default:
                //can never be here, only 1 and 2 are possible codes
        }
    }

    private long updateCalendarEntry(long eventId, String title, String location, String dateInString, String attendee){
        long startDate = helper.getStartDate(dateInString);
        long endDate = helper.getEndDate(startDate, 1);

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.DTSTART, startDate);
        values.put(CalendarContract.Events.DTEND, endDate);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        this.getApplicationContext().getContentResolver().update(updateUri, values, null, null);

        eventId = Long.parseLong(updateUri.getLastPathSegment());

        if(attendee != null){
            helper.addAttendee(this, eventId, attendee);
        }

        return eventId;
    }

    private void deleteCalendarEntry(long eventId){
        ContentResolver cr = this.getApplicationContext().getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        cr.delete(deleteUri, null, null);
    }
}
