package ch.hslu.mobpro.donotforget.todositemroomdatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TodoItemDao {
    @Query("SELECT * FROM todoItems")
    List<TodoItem> getAll();

    @Query("SELECT * FROM todoItems WHERE id LIKE :todoItemId LIMIT 1")
    TodoItem findById(int todoItemId);

    @Update
    void updateTodos(TodoItem... todoItems);

    @Insert
    void insertAllTodoItem(TodoItem... todoItems);

    @Delete
    void deleteTodoItems(TodoItem... todoItems);

    @Query("SELECT * FROM todoItems WHERE todoId LIKE :todoId")
    List<TodoItem> findTodoItemsByTodoId(int todoId);

    @Query("DELETE FROM todoItems WHERE todoid LIKE :todoId")
    void deleteTodoItemsByTodoId(int todoId);

    @Query("DELETE FROM todoItems WHERE id LIKE :todoItemId")
    void deleteTodoItemByTodoItemId(int todoItemId);
}
