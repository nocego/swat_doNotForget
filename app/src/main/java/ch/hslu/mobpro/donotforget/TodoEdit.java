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
    TodoDao todoDao;
    Todo currentTodo;
    EditText title;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("To-Do bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getDb();
        Intent intent = getIntent();
        getCurrentTodo(intent);
        fillActivity();
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
        intent.putExtra("todoId", currentTodo.id);
        startActivity(intent);
    }

    private void getDb(){
        todosDb = Room.databaseBuilder(
                this,
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();
    }

    private void getCurrentTodo(Intent intent){
        int todoId = intent.getExtras().getInt("todoId");
        todoDao = todosDb.todoDao();
        currentTodo = todoDao.findById(todoId);
    }

    private void fillActivity(){
        title = findViewById(R.id.editText4);
        title.setText(currentTodo.title);
    }

    public void udpateTodo(View v){
        currentTodo.title = title.getText().toString();
        todoDao.updateTodos(currentTodo);
        Toast.makeText(getApplicationContext(), "Ihr To-Do wurde aktualisiert.", Toast.LENGTH_LONG).show();
        openTodoDetail();
    }
}
