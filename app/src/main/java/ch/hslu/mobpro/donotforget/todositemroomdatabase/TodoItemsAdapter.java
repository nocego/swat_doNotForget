package ch.hslu.mobpro.donotforget.todositemroomdatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.hslu.mobpro.donotforget.R;

public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {
    public TodoItemsAdapter(final Context context, final List<TodoItem> todoitems){ super(context, 0, todoitems); }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final TodoItem todoitem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_todo_item_item, parent, false);
        }
        // Lookup view for data population
        final TextView title = convertView.findViewById(R.id.titleTodoItem);

        // Populate the data into the template view using the data object
        title.setText(todoitem.title);

        // Return the completed view to render on screen
        return convertView;
    }
}
