package ch.hslu.mobpro.donotforget.todosroomdatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    @Query("SELECT * FROM todos")
    List<Todo> getAll();

    @Query("SELECT * FROM todos WHERE id LIKE :todoId LIMIT 1")
    Todo findById(int todoId);

    @Update
    void updateTodos(Todo... todos);

    @Delete
    void deleteTodos(Todo... todos);

    @Insert
    void insertAll(Todo... todos);
}
