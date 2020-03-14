package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;

public class TodoEdit extends AppCompatActivity {

    private TodosDatabase todosDb;
    private TodoDao todoDao;
    private Todo currentTodo;
    private EditText title;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("To-Do bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setDb();
        final Intent intent = getIntent();
        setCurrentTodo(intent);
        fillActivity();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            openTodoDetail();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openTodoDetail(final View v){//NOPMD
        openTodoDetail();
    }

    private void openTodoDetail(){
        final Intent intent = new Intent(this, TodoDetail.class);
        intent.putExtra("todoId", currentTodo.id);
        startActivity(intent);
    }

    private void setDb(){
        todosDb = Room.databaseBuilder(
                this,
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();
    }

    private void setCurrentTodo(final Intent intent){
        final int todoId = intent.getExtras().getInt("todoId");
        todoDao = todosDb.todoDao();
        currentTodo = todoDao.findById(todoId);
    }

    private void fillActivity(){
        title = findViewById(R.id.editText4);
        title.setText(currentTodo.title);
    }

    public void udpateTodo(final View v){//NOPMD
        currentTodo.title = title.getText().toString();
        todoDao.updateTodos(currentTodo);
        Toast.makeText(getApplicationContext(), "Ihr To-Do wurde aktualisiert.", Toast.LENGTH_LONG).show();
        openTodoDetail();
    }
}
