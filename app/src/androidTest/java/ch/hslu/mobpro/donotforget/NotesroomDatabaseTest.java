package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

@MediumTest
public class NotesroomDatabaseTest {

    private final static String TEST_TITLE = "TestTitle";
    private final static String TITLE_WITH_UMLAUT = "äöüTitle";

    @Test
    public void writeRegularNoteToDb()  {
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TEST_TITLE, "TestContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeNoteWithUmlaut(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TITLE_WITH_UMLAUT, "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeEmptyNote(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote("", "");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void deleteNote(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TITLE_WITH_UMLAUT, "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest, noteDao);

        //assert
        assertEquals(noteDao.getAll().size(), numberOfNotesBeforeTest);//NOPMD
    }

    private NotesDatabase getNotesDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private Context getApplicationContext(){
        return InstrumentationRegistry
                .getTargetContext()
                .getApplicationContext();
    }

    private void waitForNotesToBecomeThatMany(final int numberOfWishedNotes, final NoteDao noteDao){//NOPMD
        List<Note> allNotes;
        do{
            allNotes = noteDao.getAll();
        }while(allNotes.size() != numberOfWishedNotes);
    }

    private Note createNote(final String title, final String content){
        final Note note = new Note();
        note.title=title;
        note.content=content;
        return note;
    }

    private NoteDao getNoteDao(){
        return getNotesDatabase().noteDao();
    }
}
