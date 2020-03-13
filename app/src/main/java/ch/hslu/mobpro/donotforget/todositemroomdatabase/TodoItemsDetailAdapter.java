package ch.hslu.mobpro.donotforget.todositemroomdatabase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ch.hslu.mobpro.donotforget.R;
import ch.hslu.mobpro.donotforget.TodoDetail;
import ch.hslu.mobpro.donotforget.TodoItemEdit;

public class TodoItemsDetailAdapter extends ArrayAdapter<TodoItem> {
    TodoDetail parent;
    public TodoItemsDetailAdapter(final Context context, final List<TodoItem> todoitems, final TodoDetail parent){
        super(context, 0, todoitems);
        this.parent = parent;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final TodoItem todoitem = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_todo_item_detail, parent, false);
        }
        // Lookup view for data population
        final TextView title = convertView.findViewById(R.id.titleTodoItem);
        final TextView date = convertView.findViewById(R.id.dateTodoItem);
        final TextView place = convertView.findViewById(R.id.placeTodoItem);
        final TextView staff = convertView.findViewById(R.id.staffTodoItem);
        final TextView inCalendar = convertView.findViewById(R.id.inCalendarTodoItem);
        final Button editButton = convertView.findViewById(R.id.button17);
        final Button deleteButton = convertView.findViewById(R.id.button16);

        final String inKalender = isInCalendar(todoitem);
        // Populate the data into the template view using the data object
        title.setText(todoitem.title);
        date.setText(todoitem.date);
        place.setText(todoitem.place);
        staff.setText(todoitem.staff);
        inCalendar.setText(inKalender);

        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View arg0){
                final Intent intent = new Intent(getContext(), TodoItemEdit.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("todoItemId", todoitem.id);
                getContext().startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View arg0){
                callCreateConfirmationAlertTodoItem(todoitem.title, todoitem.id);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public void callCreateConfirmationAlertTodoItem(final String todoItemTitle, final int todoItemId){
        this.parent.createConfirmationAlertTodoItem(todoItemTitle, todoItemId);
    }

    private String isInCalendar(final TodoItem todoItem){
        return (todoItem.inCalendar) ? "Ja" : "Nein";
    }
}
