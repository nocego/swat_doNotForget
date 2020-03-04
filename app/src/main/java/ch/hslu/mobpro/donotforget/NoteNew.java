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

import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;

public class NoteNew extends AppCompatActivity {

    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);

        renameActionBar("Neue Notiz");
        addBackButtonToActionBar();
        createNotesDb();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToNotes();
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToNotes(View v){
        backToNotes();
    }

    public void saveNewNote(View v){
        EditText title = findViewById(R.id.editText);
        EditText content = findViewById(R.id.editText2);
        Note note = new Note();
        note.title=title.getText().toString();
        note.content=content.getText().toString();
        noteDao.insertAll(note);
        Toast.makeText(getApplicationContext(), "Ihre Notiz wurde abgespeichert.", Toast.LENGTH_LONG).show();

        backToNotes();
    }

    private void renameActionBar(String name){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(name);
    }

    private void addBackButtonToActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void createNotesDb(){
        NotesDatabase notesDb = Room.databaseBuilder(
                getApplicationContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
        noteDao = notesDb.noteDao();
    }

    private void backToNotes(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentTabIndex", 0);
        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
