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
import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;

/**
 * activity to show the title and text of a saved note
 */
public class NoteDetail extends AppCompatActivity {

    private NotesDatabase notesDb;
    private NoteDao noteDao;
    Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notiz");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setNotesDb();
        final Intent intent = getIntent();
        setCurrentNote(intent);
        fillActivity();
    }

    /**
     * call backToNotes() if home button is pressed
     * @param item the tapped MenuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            backToNotes();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * open NoteEdit view with set noteId
     * @param v
     */
    public void editClicked(View v){//NOPMD
        final Intent intent = new Intent(this, NoteEdit.class);
        intent.putExtra("noteId", currentNote.id);
        startActivity(intent);
    }

    /**
     * get and show the confirmation dialog for deletion
     * @param v
     */
    public void deleteClicked(View v){//NOPMD
        AlertDialog alert = createConfirmationAlert();
        alert.show();
    }

    /**
     * get the notesDatabase and set it to class variable
     */
    private void setNotesDb(){
        notesDb = Room.databaseBuilder(
                this,
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    /**
     * get noteId from intent and set it to class variable
     * @param intent
     */
    private void setCurrentNote(final Intent intent){
        final int noteId = intent.getExtras().getInt("noteId");
        noteDao = notesDb.noteDao();
        currentNote = noteDao.findById(noteId);
    }

    /**
     * fill the textViews with the noteTitle and the noteContent
     */
    private void fillActivity() {
        final TextView title = findViewById(R.id.titleNote);
        final TextView content = findViewById(R.id.contentNote);
        title.setText(currentNote.title);
        content.setText(currentNote.content);
    }

    /**
     * create and return the confirmationAlert for deletion of this note
     * @return AlertDialog
     */
    private AlertDialog createConfirmationAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Löschen?");
        builder.setMessage("Soll die Notiz \""+currentNote.title+"\" wirklich gelöscht werden?");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                deleteCurrentNote();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        return alert;
    }

    /**
     * open the mainActivity and clear the backstack
     */
    private void backToNotes(){
        final Intent intent = new Intent(this, MainActivity.class);
        // clear back stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();
    }

    /**
     * delete the note and show a toast as information, after that call backToNotes()-Method
     */
    private void deleteCurrentNote(){
        noteDao.deleteNotes(currentNote);
        Toast.makeText(getApplicationContext(), "Ihre Notiz wurde gelöscht.", Toast.LENGTH_LONG).show();
        backToNotes();
    }
}
