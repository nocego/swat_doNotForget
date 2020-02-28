package ch.hslu.mobpro.donotforget.todosRoomDatabase;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.hslu.mobpro.donotforget.R;
import ch.hslu.mobpro.donotforget.TodoDetail;
import ch.hslu.mobpro.donotforget.TodoEdit;
import ch.hslu.mobpro.donotforget.TodoItemEdit;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsAdapter;
import ch.hslu.mobpro.donotforget.todosItemRoomDatabase.TodoItemsDatabase;

public class TodosAdapter extends ArrayAdapter<Todo> {
    private TodoItemsDatabase todoItemsDb;
    private TodoItemDao todoItemDao;
    private View convertView;
    ListView itemList;
    public TodosAdapter(Context context, ArrayList<Todo> todos){
        super(context, 0, todos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.convertView = convertView;
        getDb();
        // Get the data item for this position
        final Todo todo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            this.convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_todo_item, parent, false);
            setConvertViewListener(todo.id);
        }
        // Lookup view for data population
        TextView title = (TextView) this.convertView.findViewById(R.id.titleTodo);
        this.itemList = (ListView) this.convertView.findViewById(R.id.list3);

        // Populate the data into the template view using the data object
        title.setText(todo.title);
        List<TodoItem> allTodoItems = todoItemsDb.todoItemDao().findTodoItemsByTodoId(todo.id);
        ArrayList<TodoItem> arrayOfTodoItems = new ArrayList<>();
        for (TodoItem item : allTodoItems){
            arrayOfTodoItems.add(item);
        }
        TodoItemsAdapter adapter = new TodoItemsAdapter(this.getContext(), arrayOfTodoItems);
        itemList.setAdapter(adapter);
        setItemListListener(todo.id);
        resizeList();

        // Return the completed view to render on screen
        return this.convertView;
    }

    private void getDb(){
        todoItemsDb = Room.databaseBuilder(
                this.getContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
    }

    private void setConvertViewListener(final int todoId){
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TodoDetail.class);
                intent.putExtra("todoId", todoId);
                getContext().startActivity(intent);
            }
        });
    }

    private void setItemListListener(final int todoId){
        this.itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), TodoDetail.class);
                intent.putExtra("todoId", todoId);
                getContext().startActivity(intent);
            }
        });
    }

    private void resizeList(){
        ListAdapter listAdapter = itemList.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, itemList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = itemList.getLayoutParams();
        params.height = totalHeight + (itemList.getDividerHeight() * (listAdapter.getCount()-1));
        itemList.setLayoutParams(params);
        itemList.requestLayout();
    }
}
