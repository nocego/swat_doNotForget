package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;
import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;

public class ToDoNew extends AppCompatActivity {

    private TodoDao todoDao;

    @Override
    protected void onCreate(final Bundle savedInstanceState){//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_new);

        renameActionBar("Neues To-Do");
        addBackButtonToActionBar();
        createTodosDb();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToTodos();
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToTodos(final View v){//NOPMD
        backToTodos();
    }

    public void saveNewTodo(final View v){//NOPMD
        final EditText title = findViewById(R.id.editText3);
        final Todo todo = new Todo();
        todo.title=title.getText().toString();

        todoDao.insertAll(todo);
        Toast.makeText(getApplicationContext(), "Ihr ToDo wurde abgespeichert.", Toast.LENGTH_LONG).show();

        backToTodos();
    }

    private void renameActionBar(final String name){
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }

    private void addBackButtonToActionBar(){
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void createTodosDb(){
        final TodosDatabase todosDb = Room.databaseBuilder(
                getApplicationContext(),
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();
        todoDao = todosDb.todoDao();
    }

    private void backToTodos(){
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentTabIndex", 1);
        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
