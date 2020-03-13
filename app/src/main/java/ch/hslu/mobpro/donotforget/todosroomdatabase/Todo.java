package ch.hslu.mobpro.donotforget.todosroomdatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "todos")
public class Todo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name="title")
    public String title;

    @Override
    public boolean equals(final Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Todo)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        final Todo c = (Todo) o;

        // Compare the data members and return accordingly
        return title.equals(c.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
