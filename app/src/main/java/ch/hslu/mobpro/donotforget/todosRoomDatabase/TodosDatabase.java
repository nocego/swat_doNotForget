package ch.hslu.mobpro.donotforget.todosRoomDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodosDatabase extends RoomDatabase {
    public abstract TodoDao todoDao();
}
