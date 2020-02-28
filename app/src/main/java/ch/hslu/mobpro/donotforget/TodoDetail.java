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

import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsDetailAdapter;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.Todo;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodosDatabase;

public class TodoDetail extends AppCompatActivity {

    private TodosDatabase todosDb;
    private TodoItemsDatabase todoItemsDb;
    TodoDao todoDao;
    TodoItemDao todoItemDao;
    Todo currentTodo;
    TodoItem currentTodoItem;
    private List<TodoItem> todoItemList;
    private int currentTodoId;
    private int currentTodoItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("To-Do");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getDbs();
        getDaos();
        saveTodoId();
        saveTodoItemId();
        getCurrentTodo();
        getCurrentTodoItem();
        fillActivity();
        fillTodoItemList();
    }

    @Override
    public void onResume(){
        super.onResume();

        ArrayList<TodoItem> arrayOfTodoItems = new ArrayList<>();
        if(todoItemList != null) {
            for (TodoItem item : todoItemList) {
                arrayOfTodoItems.add(item);
            }
            TodoItemsDetailAdapter adapter = new TodoItemsDetailAdapter(getApplicationContext(), arrayOfTodoItems, this);
            final ListView list = (ListView) findViewById(R.id.list4);
            list.setAdapter(adapter);
            resizeList(list);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToTodos();
        }
        return super.onOptionsItemSelected(item);
    }

    public void editTodoClicked(View v){
        Intent intent = new Intent(this, TodoEdit.class);
        intent.putExtra("todoId", currentTodo.id);
        startActivity(intent);
    }

    public void deleteTodoClicked(View v){
        createConfirmationAlert();
    }

    private void getDbs(){
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

    private void getDaos(){
        todoDao = todosDb.todoDao();
        todoItemDao = todoItemsDb.todoItemDao();
    }

    private void getCurrentTodo(){
        currentTodo = todoDao.findById(currentTodoId);
    }

    private void getCurrentTodoItem() { currentTodoItem = todoItemDao.findById(currentTodoItemId); }

    private void fillActivity() {
        TextView title = (TextView) findViewById(R.id.titleTodo);
        title.setText(currentTodo.title);
    }

    private void createConfirmationAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll das To-Do \""+currentTodo.title+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentTodo();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void backToTodos(){
        Intent intent = new Intent(this, MainActivity.class);
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

    private void deleteTodoItemsFromCalendar(List<TodoItem> deleteList){
        for(TodoItem item:deleteList){
            deleteCalendarEntry(item.calendarEventId);
        }
    }

    public void addTodoClicked(View view){
        Intent intent = new Intent(this, TodoItemNew.class);
        intent.putExtra("todoId", currentTodo.id);
        startActivity(intent);
    }

    private void fillTodoItemList(){
        todoItemList = todoItemDao.findTodoItemsByTodoId(this.currentTodoId);
    }

    private void saveTodoId(){
        Intent intent = getIntent();
        this.currentTodoId = intent.getExtras().getInt("todoId");
    }

    private void saveTodoItemId(){
        Intent intent = getIntent();
        this.currentTodoItemId = intent.getExtras().getInt("todoItemId");
    }

    private void resizeList(ListView list){
        ListAdapter listAdapter = list.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, list);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = totalHeight + (list.getDividerHeight() * (listAdapter.getCount()-1));
        list.setLayoutParams(params);
        list.requestLayout();
    }

    public void createConfirmationAlertTodoItem(String todoItemTitle, final int todoItemId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll das To-Do-Item \""+todoItemTitle+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTodoItem(todoItemId);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteTodoItem(int todoItemId){
        deleteCalendarEntry(todoItemDao.findById(todoItemId).calendarEventId);
        todoItemDao.deleteTodoItemByTodoItemId(todoItemId);
        finish();
        startActivity(getIntent());
        Toast.makeText(getApplicationContext(), "Ihr To-Do-Item wurde gelöscht.", Toast.LENGTH_LONG).show();
    }

    private void deleteCalendarEntry(long eventId){
        if(eventId != -1) {
            ContentResolver cr = this.getApplicationContext().getContentResolver();
            Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
            int rows = cr.delete(deleteUri, null, null);
        }
    }
}
