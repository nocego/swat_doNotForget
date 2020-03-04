
package ch.hslu.mobpro.donotforget.todositemroomdatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {TodoItem.class}, version = 1)
public abstract class TodoItemsDatabase extends RoomDatabase{
    public abstract TodoItemDao todoItemDao();
}
