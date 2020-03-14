package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesAdapter;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;


public class NotesFragment extends Fragment {

    private NotesDatabase notesDb;
    private List<Note> noteList;

    @Override
    public void onCreate(Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {//NOPMD
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {//NOPMD
        super.onViewCreated(view, savedInstanceState);

        setDb();
        fillNoteList();

        final ArrayList<Note> arrayOfNotes = new ArrayList<>(noteList);
        final NotesAdapter adapter = new NotesAdapter(this.getContext(), arrayOfNotes);
        final ListView list = this.getView().findViewById(R.id.list);
        list.setAdapter(adapter);

        addOnItemClickListener(list);
    }

    private void setDb(){
        notesDb = Room.databaseBuilder(
                this.getContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private void fillNoteList(){
        final NoteDao noteDao = notesDb.noteDao();
        noteList = noteDao.getAll();
    }

    private void addOnItemClickListener(final ListView list){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//NOPMD
                final Note clickedNote = (Note)list.getItemAtPosition(position);

                final Intent intent = new Intent(getContext(), NoteDetail.class);
                intent.putExtra("noteId", clickedNote.id);
                startActivity(intent);
            }
        });
    }


}
