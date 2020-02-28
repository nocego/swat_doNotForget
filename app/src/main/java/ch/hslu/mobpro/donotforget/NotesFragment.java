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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.hslu.mobpro.donotforget.notesRoomDatabase.Note;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NotesAdapter;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NotesDatabase;


public class NotesFragment extends Fragment {

    private NotesDatabase notesDb;
    private NoteDao noteDao;
    private List<Note> noteList;

    public NotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDb();
        fillNoteList();

        ArrayList<Note> arrayOfNotes = new ArrayList<Note>();
        for(Note item : noteList){
            arrayOfNotes.add(item);
        }
        NotesAdapter adapter = new NotesAdapter(this.getContext(), arrayOfNotes);
        final ListView list = (ListView) this.getView().findViewById(R.id.list);
        list.setAdapter(adapter);

        addOnItemClickListener(list);
    }

    private void getDb(){
        notesDb = Room.databaseBuilder(
                this.getContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private void fillNoteList(){
        noteDao = notesDb.noteDao();
        noteList = noteDao.getAll();
    }

    private void addOnItemClickListener(final ListView list){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note clickedNote = (Note)list.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), NoteDetail.class);
                intent.putExtra("noteId", clickedNote.id);
                startActivity(intent);
            }
        });
    }


}