package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.Note;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NotesDatabase;

public class NoteDetail extends AppCompatActivity {

    private NotesDatabase notesDb;
    NoteDao noteDao;
    Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notiz");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getDb();
        Intent intent = getIntent();
        getCurrentNote(intent);
        fillActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToNotes();
        }
        return super.onOptionsItemSelected(item);
    }

    public void editClicked(View v){
        Intent intent = new Intent(this, NoteEdit.class);
        intent.putExtra("noteId", currentNote.id);
        startActivity(intent);
    }

    public void deleteClicked(View v){
        createConfirmationAlert();
    }

    private void getDb(){
        notesDb = Room.databaseBuilder(
                this,
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private void getCurrentNote(Intent intent){
        int noteId = intent.getExtras().getInt("noteId");
        noteDao = notesDb.noteDao();
        currentNote = noteDao.findById(noteId);
    }

    private void fillActivity() {
        TextView title = (TextView) findViewById(R.id.titleNote);
        TextView content = (TextView) findViewById(R.id.contentNote);
        title.setText(currentNote.title);
        content.setText(currentNote.content);
    }

    private void createConfirmationAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll die Notiz \""+currentNote.title+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentNote();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void backToNotes(){
        Intent intent = new Intent(this, MainActivity.class);
        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

    private void deleteCurrentNote(){
        noteDao.deleteNotes(currentNote);
        Toast.makeText(getApplicationContext(), "Ihre Notiz wurde gelöscht.", Toast.LENGTH_LONG).show();
        backToNotes();
    }
}
