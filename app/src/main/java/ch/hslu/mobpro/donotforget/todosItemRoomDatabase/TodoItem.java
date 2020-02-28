package ch.hslu.mobpro.donotforget.todosItemRoomDatabase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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
}
