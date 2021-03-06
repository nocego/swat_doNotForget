package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.Locale;

import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDatabase;

public class TodoItemNew extends AppCompatActivity implements TimePickerFragment.TimeListener, DatePickerFragment.DateListener {

    private TodoItemDao todoItemDao;
    private int currentTodoId;
    private final Calendar calendar = Calendar.getInstance();
    private boolean canWriteToCalendar = false;//NOPMD
    private long calendarEventId = -1;
    private CheckBox calendarCheckbox;
    private TodoItemHelper helper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item_new);
        calendarCheckbox = findViewById(R.id.checkBox);

        renameActionBar("Neues To-Do Item");
        addBackButtonToActionBar();
        createTodoItemsDb();

        setTodoIdFromIntent();
        helper = new TodoItemHelper();
        canWriteToCalendar = helper.askPermissionReadCalendar(this);
        canWriteToCalendar = helper.askPermissionWriteCalendar(this);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openTodoDetail();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTodoDetail(final View v) {//NOPMD
        openTodoDetail();
    }

    private void openTodoDetail() {
        final Intent intent = new Intent(this, TodoDetail.class);
        intent.putExtra("todoId", currentTodoId);
        startActivity(intent);
    }

    public void saveNewTodoItem(final View v) {//NOPMD
        final EditText title = findViewById(R.id.editText5);
        final Button date = findViewById(R.id.dateButton);
        final Button time = findViewById(R.id.timeButton);
        final EditText place = findViewById(R.id.editText6);
        final EditText staff = findViewById(R.id.editText8);
        final CheckBox inCalendar = findViewById(R.id.checkBox);

        CharSequence dateFinal = "01.01.1970";
        if (!date.getText().equals("Datum")) {
            dateFinal = date.getText();
        }

        CharSequence timeFinal = "00:00";
        if (!time.getText().equals("Zeit")) {
            timeFinal = time.getText();
        }
        final TodoItem todoitem = new TodoItem();
        todoitem.todoId = currentTodoId;
        todoitem.title = title.getText().toString();
        todoitem.date = dateFinal + " " + timeFinal;
        todoitem.place = place.getText().toString();
        todoitem.staff = staff.getText().toString();
        todoitem.inCalendar = inCalendar.isChecked();
        if (!canWriteToCalendar) {
            todoitem.inCalendar = false;
        }


        if (inCalendar.isChecked() && canWriteToCalendar) {
            this.calendarEventId = helper.writeToCal(this, todoitem.date, todoitem.title, todoitem.place, todoitem.staff);
        }
        todoitem.calendarEventId = this.calendarEventId;

        todoItemDao.insertAllTodoItem(todoitem);
        Toast.makeText(getApplicationContext(), "Ihr ToDo-Item wurde abgespeichert.", Toast.LENGTH_LONG).show();

        openTodoDetail();
    }

    private void renameActionBar(final String name) {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }

    private void addBackButtonToActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void createTodoItemsDb() {
        final TodoItemsDatabase todoItemsDb = Room.databaseBuilder(
                getApplicationContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
        todoItemDao = todoItemsDb.todoItemDao();
    }

    private void setTodoIdFromIntent() {
        currentTodoId = getIntent().getExtras().getInt("todoId");
    }

    private void updateDateButtonLabel() {
        final String year = String.valueOf(this.calendar.get(Calendar.YEAR));
        final String month = String.format(Locale.GERMANY, "%02d", this.calendar.get(Calendar.MONTH) + 1);
        final String day = String.format(Locale.GERMANY, "%02d", this.calendar.get(Calendar.DAY_OF_MONTH));

        final Button button = findViewById(R.id.dateButton);
        button.setText(String.format("%s.%s.%s", day, month, year));
    }

    private void updateTimeButtonLabel() {
        final String hour = String.format(Locale.GERMANY, "%02d", this.calendar.get(Calendar.HOUR_OF_DAY));
        final String minute = String.format(Locale.GERMANY, "%02d", this.calendar.get(Calendar.MINUTE));

        final Button button = findViewById(R.id.timeButton);
        button.setText(String.format("%s:%s", hour, minute));
    }

    @Override
    public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
        this.calendar.set(year, month, day);
        this.updateDateButtonLabel();
    }

    public void dateButtonClicked(final View v) {//NOPMD
        final DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void timeButtonClicked(final View v) {//NOPMD
        final DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(Calendar.MINUTE, minute);
        this.updateTimeButtonLabel();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canWriteToCalendar = true;
                    Toast.makeText(TodoItemNew.this, "Permisson granted!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    canWriteToCalendar = false;
                    Toast.makeText(TodoItemNew.this, "Keine Todos werden in den Kalender gespeichert.", Toast.LENGTH_LONG).show();
                    calendarCheckbox.setEnabled(false);
                }
                break;
            default:
                //can never be here, only 1 and 2 are possible codes
        }
    }
}
