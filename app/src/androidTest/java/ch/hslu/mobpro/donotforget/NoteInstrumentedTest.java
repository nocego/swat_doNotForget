package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.List;

import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class NoteInstrumentedTest {

    @Rule
    public ActivityTestRule<NoteNew> NoteNewActivityTestRule = new ActivityTestRule<NoteNew>(NoteNew.class){
        protected Intent getActivityIntent(){
            InstrumentationRegistry.getTargetContext();
            return new Intent(Intent.ACTION_MAIN);
        }
    };

    @Rule
    public ActivityTestRule<NoteDetail> NoteDetailActivityTestRule = new ActivityTestRule<NoteDetail>(NoteDetail.class){
        protected Intent getActivityIntent(){
            InstrumentationRegistry.getTargetContext();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("noteId", getDemoDatabaseEntry(NoteNewActivityTestRule.getActivity()).id);
            return intent;
        }
    };

    @Test
    public void testWriteNoteToDb(){
        //arrange
        final NoteNew activityNoteNew = NoteNewActivityTestRule.getActivity();

        //act
        createNote(activityNoteNew);
        Note lastSavedNote = getLastNoteFromDb(activityNoteNew);

        //assert
        assertEquals("TestNote",lastSavedNote.title);

        //revert
        deleteNote(activityNoteNew, lastSavedNote);
        //also delete the one made from NoteDetail
        deleteNote(activityNoteNew, getLastNoteFromDb(activityNoteNew));
    }

    @Test
    public void testShowTitleCorrectFromDb(){
        //arrange
        final NoteDetail activityNoteDetail = NoteDetailActivityTestRule.getActivity();
        TextView title = activityNoteDetail.findViewById(R.id.titleNote);

        //assert
        assertEquals(getLastNoteFromDb(activityNoteDetail).title, title.getText().toString());

        //revert
        deleteNote(activityNoteDetail, getLastNoteFromDb(activityNoteDetail));
    }

    @Test
    public void testDeleteNote(){
        //arrange
        NoteDetail activitNoteDetail = NoteDetailActivityTestRule.getActivity();
        Method method = null;
        try {
            method = NoteDetail.class.getDeclaredMethod("deleteCurrentNote");
        }catch (Exception ex){
            Log.e("Exception", "methodNotDeclaredError");
        }
        method.setAccessible(true);

        //act
        activitNoteDetail.currentNote = getLastNoteFromDb(activitNoteDetail);
        try {
            method.invoke(activitNoteDetail);
        }catch (Exception ex){
            Log.e("Exception", "invokeError");
        }

        //assert
        assertNotEquals("TestNote", getLastNoteFromDb(activitNoteDetail).title);
    }

    private Note getDemoDatabaseEntry(AppCompatActivity activity){
        NoteDao noteDao = getNoteDao(activity);

        Note note = new Note();
        note.title = "TestNote";
        noteDao.insertAll(note);

        return getLastNoteFromDb(activity);
    }

    private NoteDao getNoteDao(AppCompatActivity activity){
        NotesDatabase notesDb = Room.databaseBuilder(
                activity.getApplicationContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
        return notesDb.noteDao();
    }

    private Note getLastNoteFromDb(AppCompatActivity activity){
        NoteDao noteDao = getNoteDao(activity);
        List<Note> allNotes = noteDao.getAll();
        if(allNotes.size() > 0) {
            return allNotes.get(allNotes.size() - 1);
        }else{
            Note defaultNote = new Note();
            defaultNote.id = -1;
            defaultNote.title = "defaultNote";
            defaultNote.content = "defaultNote";
            return defaultNote;
        }
    }

    private void createNote(final NoteNew activity){
        NoteDao noteDao = getNoteDao(activity);
        int oldListSize = noteDao.getAll().size();
        final EditText title = activity.findViewById(R.id.editText);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title.setText("TestNote");
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View v = activity.findViewById(R.id.content);
                activity.saveNewNote(v);
            }
        });


        //wait for note to be saved in db
        List<Note> allNotes = null;
        do{
            allNotes = noteDao.getAll();
        }while(allNotes.size() == oldListSize);
    }

    private void deleteNote(AppCompatActivity activity, Note note){
        NoteDao noteDao = getNoteDao(activity);
        noteDao.deleteNotes(note);
    }
}
