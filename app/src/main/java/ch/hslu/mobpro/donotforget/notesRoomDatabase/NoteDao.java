package ch.hslu.mobpro.donotforget.notesRoomDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("SELECT * FROM notes")
    List<Note> getAll();

    @Query("SELECT * FROM notes WHERE id LIKE :noteId LIMIT 1")
    Note findById(int noteId);

    @Update
    void updateNotes(Note... notes);

    @Delete
    void deleteNotes(Note... notes);

    @Insert
    void insertAll(Note... notes);
}
