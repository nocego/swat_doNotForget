package ch.hslu.mobpro.donotforget.notesroomdatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.hslu.mobpro.donotforget.R;

public class NotesAdapter extends ArrayAdapter<Note> {
    public NotesAdapter(Context context, List<Note> notes){
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_note_item, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.titleNote);
        TextView content = convertView.findViewById(R.id.content);
        // Populate the data into the template view using the data object
        title.setText(note.title);
        content.setText(note.content);
        // Return the completed view to render on screen
        return convertView;
    }
}
