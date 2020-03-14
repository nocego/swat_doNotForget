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

public class NoteEdit extends AppCompatActivity {

    private NotesDatabase notesDb;
    private NoteDao noteDao;
    private Note currentNote;
    private EditText title;
    private EditText content;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notiz bearbeiten");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setDb();
        final Intent intent = getIntent();
        setCurrentNote(intent);
        fillActivity();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            openNoteDetail();
        }
        return super.onOptionsItemSelected(item);
    }

    public void openNoteDetail(View v){//NOPMD
        openNoteDetail();
    }

    private void openNoteDetail(){
        final Intent intent = new Intent(this, NoteDetail.class);
        intent.putExtra("noteId", currentNote.id);
        startActivity(intent);
    }

    private void setDb(){
        notesDb = Room.databaseBuilder(
                this,
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private void setCurrentNote(final Intent intent){
        final int noteId = intent.getExtras().getInt("noteId");
        noteDao = notesDb.noteDao();
        currentNote = noteDao.findById(noteId);
    }

    private void fillActivity(){
        title = findViewById(R.id.editText);
        content = findViewById(R.id.editText2);
        title.setText(currentNote.title);
        content.setText(currentNote.content);
    }

    public void updateNote(View v){//NOPMD
        currentNote.title = title.getText().toString();
        currentNote.content = content.getText().toString();
        noteDao.updateNotes(currentNote);
        Toast.makeText(getApplicationContext(), "Ihre Notiz wurde aktualisiert.", Toast.LENGTH_LONG).show();
        openNoteDetail();
    }
}
