package ch.hslu.mobpro.donotforget.notesroomdatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name="title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Note)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Note c = (Note) o;

        // Compare the data members and return accordingly
        return title.equals(c.title)
                && content.equals(c.content);
    }
}
