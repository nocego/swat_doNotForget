package ch.hslu.mobpro.donotforget;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import static org.junit.Assert.*;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class UnitTests {
    @Test
    public void writeRegularNoteToDb()  {
        //arrange
        NotesDatabase notesDb = getNotesDatabase();
        NoteDao noteDao = notesDb.noteDao();
        int numberOfNotesBeforeTest = noteDao.getAll().size();
        Note note = createNote("TestTitle", "TestContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeNoteWithUmlaut(){
        //arrange
        NoteDao noteDao = getNoteDao();
        int numberOfNotesBeforeTest = noteDao.getAll().size();
        Note note = createNote("äöüTitle", "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeEmptyNote(){
        //arrange
        NoteDao noteDao = getNoteDao();
        int numberOfNotesBeforeTest = noteDao.getAll().size();
        Note note = createNote("", "");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void deleteNote(){
        //arrange
        NoteDao noteDao = getNoteDao();
        int numberOfNotesBeforeTest = noteDao.getAll().size();
        Note note = createNote("äöüTitle", "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        List<Note> noteList = noteDao.getAll();
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest, noteDao);

        //assert
        assertEquals(noteDao.getAll().size(), numberOfNotesBeforeTest);
    }

    @Test
    public void writeRegularTodo(){
        //arrange
        TodoDao todoDao = getTodoDao();
        int numberOfTodosBeforeTest = todoDao.getAll().size();
        Todo todo = createTodo("TestTitle");

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void writeTodoWithUmlaut(){
        //arrange
        TodoDao todoDao = getTodoDao();
        int numberOfTodosBeforeTest = todoDao.getAll().size();
        Todo todo = createTodo("äöüTitle");

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void writeEmptyTodo(){
        //arrange
        TodoDao todoDao = getTodoDao();
        int numberOfTodosBeforeTest = todoDao.getAll().size();
        Todo todo = createTodo("");

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void deleteTodo(){
        //arrange
        TodoDao todoDao = getTodoDao();
        int numberOfTodosBeforeTest = todoDao.getAll().size();
        Todo todo = createTodo("äöüTitle");

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        List<Todo> noteList = todoDao.getAll();
        todoDao.deleteTodos(noteList.get(numberOfTodosBeforeTest));
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest, todoDao);

        //assert
        assertEquals(todoDao.getAll().size(), numberOfTodosBeforeTest);
    }

    @Test
    public void writeRegularTodoItem()  {
        //arrange
        TodoDao todoDao = getTodoDao();
        int numberOfTodosBeforeTest = todoDao.getAll().size();
        Todo todo = createTodo("TestTitle");
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        List<Todo> todoList = todoDao.getAll();
        Todo createdTodo = todoList.get(numberOfTodosBeforeTest);

        TodoItemsDatabase todoItemsDb = getTodoItemsDatabase();
        TodoItemDao todoItemDao = todoItemsDb.todoItemDao();
        int numberOFTodoItemsBeforeTest = todoItemDao.getAll().size();

        TodoItem todoItem = createTodoItem(createdTodo.id, "TestTitle", "01.01.2020", "Rotkreuz", "Gerold", false);

        //act
        todoItemDao.insertAllTodoItem(todoItem);
        waitForTodoItemsToBecomeThatMany(numberOFTodoItemsBeforeTest+1, todoItemDao);
        List<TodoItem> todoItemList = todoItemDao.getAll();

        //assert
        assertEquals(todoItem, todoItemList.get(numberOFTodoItemsBeforeTest));

        //revert
        todoItemDao.deleteTodoItems(todoItemList.get(numberOFTodoItemsBeforeTest));
        waitForTodoItemsToBecomeThatMany(numberOfTodosBeforeTest, todoItemDao);
        todoDao.deleteTodos(createdTodo);
    }

    //todo: unittests for not all variables in todoitem
    //delete todo with todoitems in it

    private NoteDao getNoteDao(){
        return getNotesDatabase().noteDao();
    }
    
    private TodoDao getTodoDao(){
        return getTodoDatabase().todoDao();
    }

    private NotesDatabase getNotesDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private TodosDatabase getTodoDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();
    }

    private TodoItemsDatabase getTodoItemsDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
    }

    private Context getApplicationContext(){
        return InstrumentationRegistry
                .getTargetContext()
                .getApplicationContext();
    }

    private void waitForNotesToBecomeThatMany(int numberOfWishedNotes, NoteDao noteDao){
        List<Note> allNotes;
        do{
            allNotes = noteDao.getAll();
        }while(allNotes.size() != numberOfWishedNotes);
    }

    private void waitForTodosToBecomeThatMany(int numberOfWishedTodos, TodoDao todoDao){
        List<Todo> allTodos;
        do{
            allTodos = todoDao.getAll();
        }while (allTodos.size() != numberOfWishedTodos);
    }

    private void waitForTodoItemsToBecomeThatMany(int numberOfWishedTodoItems, TodoItemDao todoItemDao){
        List<TodoItem> allTodoItems;
        do{
            allTodoItems = todoItemDao.getAll();
        }while (allTodoItems.size() != numberOfWishedTodoItems);
    }

    private Note createNote(String title, String content){
        Note note = new Note();
        note.title=title;
        note.content=content;
        return note;
    }

    private Todo createTodo(String title){
        Todo todo = new Todo();
        todo.title=title;
        return todo;
    }

    private TodoItem createTodoItem(int todoId, String title, String date, String place, String staff, boolean inCalendar){
        TodoItem todoItem = new TodoItem();
        todoItem.todoId = todoId;
        todoItem.title = title;
        todoItem.date = date;
        todoItem.place = place;
        todoItem.staff = staff;
        todoItem.inCalendar = inCalendar;
        return todoItem;
    }
}
