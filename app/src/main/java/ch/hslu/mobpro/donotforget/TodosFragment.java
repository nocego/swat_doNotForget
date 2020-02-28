package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.Todo;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodosAdapter;
import ch.hslu.mobpro.donotforget.todosRoomDatabase.TodosDatabase;


public class TodosFragment extends Fragment {

    private TodosDatabase todosDb;
    private TodoDao todoDao;
    private List<Todo> todoList;
    private TodoItemsDatabase todoItemsDb;
    private TodoItemDao todoItemDao;
    private List<TodoItem> todoItemList;

    public TodosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todos, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        getDb();
        fillTodoList();

        ArrayList<Todo> arrayOfTodos = new ArrayList<>();
        for(Todo item : todoList){
            arrayOfTodos.add(item);
        }
        TodosAdapter adapter = new TodosAdapter(this.getContext(), arrayOfTodos);
        final ListView list = this.getView().findViewById(R.id.list1);
        list.setAdapter(adapter);
        addOnItemClickListener(list);
    }

    private void getDb(){
        todosDb = Room.databaseBuilder(
                this.getContext(),
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();

        todoItemsDb = Room.databaseBuilder(
                this.getContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
    }

    private void fillTodoList(){
        todoDao = todosDb.todoDao();
        todoList = todoDao.getAll();
        todoItemDao = todoItemsDb.todoItemDao();
        todoItemList = todoItemDao.getAll();
    }

    private void addOnItemClickListener(final ListView list){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Todo clickedTodo = (Todo)list.getItemAtPosition(position);
                Log.e("asdf", "clicked");

                Intent intent = new Intent(getContext(), TodoDetail.class);
                intent.putExtra("todoId", clickedTodo.id);
                startActivity(intent);
            }
        });
    }
}
