package ch.hslu.mobpro.donotforget;

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

    private final static String TEST_TITLE = "TestTitle";
    private final static String TITLE_WITH_UMLAUT = "äöüTitle";

    @Test
    public void writeRegularNoteToDb()  {
        //arrange
        final NotesDatabase notesDb = getNotesDatabase();
        final NoteDao noteDao = notesDb.noteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TEST_TITLE, "TestContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeNoteWithUmlaut(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TITLE_WITH_UMLAUT, "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void writeEmptyNote(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote("", "");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();

        //assert
        assertEquals(note, noteList.get(numberOfNotesBeforeTest));//NOPMD

        //revert
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
    }

    @Test
    public void deleteNote(){
        //arrange
        final NoteDao noteDao = getNoteDao();
        final int numberOfNotesBeforeTest = noteDao.getAll().size();//NOPMD
        final Note note = createNote(TITLE_WITH_UMLAUT, "äöüContent");

        //act
        noteDao.insertAll(note);
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest+1, noteDao);
        final List<Note> noteList = noteDao.getAll();
        noteDao.deleteNotes(noteList.get(numberOfNotesBeforeTest));
        waitForNotesToBecomeThatMany(numberOfNotesBeforeTest, noteDao);

        //assert
        assertEquals(noteDao.getAll().size(), numberOfNotesBeforeTest);//NOPMD
    }

    @Test
    public void writeRegularTodo(){
        //arrange
        final TodoDao todoDao = getTodoDao();
        final int numberOfTodosBeforeTest = todoDao.getAll().size();//NOPMD
        final Todo todo = createTodo(TEST_TITLE);

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        final List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));//NOPMD

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void writeTodoWithUmlaut(){
        //arrange
        final TodoDao todoDao = getTodoDao();
        final int numberOfTodosBeforeTest = todoDao.getAll().size();//NOPMD
        final Todo todo = createTodo(TITLE_WITH_UMLAUT);

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        final List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));//NOPMD

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void writeEmptyTodo(){
        //arrange
        final TodoDao todoDao = getTodoDao();
        final int numberOfTodosBeforeTest = todoDao.getAll().size();//NOPMD
        final Todo todo = createTodo("");

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        final List<Todo> todoList = todoDao.getAll();

        //assert
        assertEquals(todo, (todoList.get(numberOfTodosBeforeTest)));//NOPMD

        //revert
        todoDao.deleteTodos(todoList.get(numberOfTodosBeforeTest));
    }

    @Test
    public void deleteTodo(){
        //arrange
        final TodoDao todoDao = getTodoDao();
        final int numberOfTodosBeforeTest = todoDao.getAll().size();//NOPMD
        final Todo todo = createTodo(TITLE_WITH_UMLAUT);

        //act
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        final List<Todo> noteList = todoDao.getAll();
        todoDao.deleteTodos(noteList.get(numberOfTodosBeforeTest));
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest, todoDao);

        //assert
        assertEquals(todoDao.getAll().size(), numberOfTodosBeforeTest);//NOPMD
    }

    @Test
    public void writeRegularTodoItem()  {
        //arrange
        final TodoDao todoDao = getTodoDao();
        final int numberOfTodosBeforeTest = todoDao.getAll().size();//NOPMD
        final Todo todo = createTodo(TEST_TITLE);
        todoDao.insertAll(todo);
        waitForTodosToBecomeThatMany(numberOfTodosBeforeTest+1, todoDao);
        final List<Todo> todoList = todoDao.getAll();
        final Todo createdTodo = todoList.get(numberOfTodosBeforeTest);

        final TodoItemsDatabase todoItemsDb = getTodoItemsDatabase();
        final TodoItemDao todoItemDao = todoItemsDb.todoItemDao();
        final int numberOFTodoItemsBeforeTest = todoItemDao.getAll().size();//NOPMD

        final TodoItem todoItem = createTodoItem(createdTodo.id, TEST_TITLE, "01.01.2020", "Rotkreuz", "Gerold", false);

        //act
        todoItemDao.insertAllTodoItem(todoItem);
        waitForTodoItemsToBecomeThatMany(numberOFTodoItemsBeforeTest+1, todoItemDao);
        final List<TodoItem> todoItemList = todoItemDao.getAll();

        //assert
        assertEquals(todoItem, todoItemList.get(numberOFTodoItemsBeforeTest));//NOPMD

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

    private void waitForNotesToBecomeThatMany(final int numberOfWishedNotes, final NoteDao noteDao){//NOPMD
        List<Note> allNotes;
        do{
            allNotes = noteDao.getAll();
        }while(allNotes.size() != numberOfWishedNotes);
    }

    private void waitForTodosToBecomeThatMany(final int numberOfWishedTodos, final TodoDao todoDao){//NOPMD
        List<Todo> allTodos;
        do{
            allTodos = todoDao.getAll();
        }while (allTodos.size() != numberOfWishedTodos);
    }

    private void waitForTodoItemsToBecomeThatMany(final int numberOfWishedTodoItems, final TodoItemDao todoItemDao){//NOPMD
        List<TodoItem> allTodoItems;
        do{
            allTodoItems = todoItemDao.getAll();
        }while (allTodoItems.size() != numberOfWishedTodoItems);
    }

    private Note createNote(final String title, final String content){
        final Note note = new Note();
        note.title=title;
        note.content=content;
        return note;
    }

    private Todo createTodo(final String title){
        final Todo todo = new Todo();
        todo.title=title;
        return todo;
    }

    private TodoItem createTodoItem(final int todoId, final String title, final String date, final String place, final String staff, final boolean inCalendar){
        final TodoItem todoItem = new TodoItem();
        todoItem.todoId = todoId;
        todoItem.title = title;
        todoItem.date = date;
        todoItem.place = place;
        todoItem.staff = staff;
        todoItem.inCalendar = inCalendar;
        return todoItem;
    }
}
