package ch.hslu.mobpro.donotforget.todositemroomdatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "todoItems")
public class TodoItem {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name="todoId")
    public int todoId;

    @ColumnInfo(name="title")
    public String title;

    @ColumnInfo(name="date")
    public String date;

    @ColumnInfo(name="place")
    public String place;

    @ColumnInfo(name="staff")
    public String staff;

    @ColumnInfo(name="inCalendar")
    public boolean inCalendar;

    @ColumnInfo(name="calendarEventId")
    public long calendarEventId;

    @Override
    public boolean equals(final Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof TodoItem)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        final TodoItem c = (TodoItem) o;

        // Compare the data members and return accordingly
        return todoId == c.todoId
                && title.equals(c.title)
                && date.equals(c.date)
                && place.equals(c.place)
                && staff.equals(c.staff)
                && inCalendar == c.inCalendar;
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, title, date, place, staff, inCalendar, calendarEventId);
    }
}
