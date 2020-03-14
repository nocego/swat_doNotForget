package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDetailAdapter;
import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;

public class TodoDetail extends AppCompatActivity {

    private TodosDatabase todosDb;
    private TodoItemsDatabase todoItemsDb;
    private TodoDao todoDao;
    private TodoItemDao todoItemDao;
    private Todo currentTodo;
    private List<TodoItem> todoItemList;
    private int currentTodoId;
    private static final String TODO_ID_STRING = "todoId";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("To-Do");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setDbs();
        setDaos();
        saveTodoId();
        setCurrentTodo();
        fillActivity();
        fillTodoItemList();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(todoItemList != null) {
            final ArrayList<TodoItem> arrayOfTodoItems = new ArrayList<>(todoItemList);
            final TodoItemsDetailAdapter adapter = new TodoItemsDetailAdapter(getApplicationContext(), arrayOfTodoItems, this);
            final ListView list = findViewById(R.id.list4);
            list.setAdapter(adapter);
            resizeList(list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToTodos();
        }
        return super.onOptionsItemSelected(item);
    }

    public void editTodoClicked(View v){//NOPMD
        final Intent intent = new Intent(this, TodoEdit.class);
        intent.putExtra(TODO_ID_STRING, currentTodo.id);
        startActivity(intent);
    }

    public void deleteTodoClicked(View v){//NOPMD
        createConfirmationAlert();
    }

    private void setDbs(){
        todosDb = Room.databaseBuilder(
                this,
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();

        todoItemsDb = Room.databaseBuilder(
                this,
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
    }

    private void setDaos(){
        todoDao = todosDb.todoDao();
        todoItemDao = todoItemsDb.todoItemDao();
    }

    private void setCurrentTodo(){
        currentTodo = todoDao.findById(currentTodoId);
    }

    private void fillActivity() {
        final TextView title = findViewById(R.id.titleTodo);
        title.setText(currentTodo.title);
    }

    private void createConfirmationAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll das To-Do \""+currentTodo.title+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deleteCurrentTodo();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void backToTodos(){
        final Intent intent = new Intent(this, MainActivity.class);
        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("currentTabIndex", 1);
        startActivity(intent);
        finish();
    }

    private void deleteCurrentTodo(){
        deleteTodoItemsFromCalendar(todoItemDao.findTodoItemsByTodoId(currentTodoId));
        todoDao.deleteTodos(currentTodo);
        todoItemDao.deleteTodoItemsByTodoId(currentTodoId);
        Toast.makeText(getApplicationContext(), "Ihr To-Do wurde gelöscht.", Toast.LENGTH_LONG).show();
        backToTodos();
    }

    private void deleteTodoItemsFromCalendar(final List<TodoItem> deleteList){
        for(final TodoItem item:deleteList){
            deleteCalendarEntry(item.calendarEventId);
        }
    }

    public void addTodoClicked(final View view){
        final Intent intent = new Intent(this, TodoItemNew.class);
        intent.putExtra(TODO_ID_STRING, currentTodo.id);
        startActivity(intent);
    }

    private void fillTodoItemList(){
        todoItemList = todoItemDao.findTodoItemsByTodoId(this.currentTodoId);
    }

    private void saveTodoId(){
        final Intent intent = getIntent();
        this.currentTodoId = intent.getExtras().getInt(TODO_ID_STRING);
    }

    private void resizeList(final ListView list){
        final ListAdapter listAdapter = list.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            final View listItem = listAdapter.getView(i, null, list);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        final ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight + (list.getDividerHeight() * (listAdapter.getCount()-1));
        list.setLayoutParams(params);
        list.requestLayout();
    }

    public void createConfirmationAlertTodoItem(final String todoItemTitle, final int todoItemId){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll das To-Do-Item \""+todoItemTitle+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deleteTodoItem(todoItemId);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteTodoItem(final int todoItemId){
        deleteCalendarEntry(todoItemDao.findById(todoItemId).calendarEventId);
        todoItemDao.deleteTodoItemByTodoItemId(todoItemId);
        finish();
        startActivity(getIntent());
        Toast.makeText(getApplicationContext(), "Ihr To-Do-Item wurde gelöscht.", Toast.LENGTH_LONG).show();
    }

    private void deleteCalendarEntry(final long eventId){
        if(eventId != -1) {
            final ContentResolver contRes = this.getApplicationContext().getContentResolver();
            final Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            contRes.delete(deleteUri, null, null);
        }
    }
}
