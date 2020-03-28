package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItem;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemDao;
import ch.hslu.mobpro.donotforget.todositemroomdatabase.TodoItemsDatabase;
import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class todositemroomDatabaseTest {

    private final static String TEST_TITLE = "TestTitle";
    private final static String TITLE_WITH_UMLAUT = "äöüTitle";

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

        final TodoItem todoItem = createTodoItem(createdTodo.id, TEST_TITLE, "01.01.2020 14:00", "Rotkreuz", "Gerold", false);

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

    @Test
    public void writeTodoItemWithUmlaut()  {
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

        final TodoItem todoItem = createTodoItem(createdTodo.id, TITLE_WITH_UMLAUT, "01.01.2020 14:00", "Rötkreuz", "Geröld", false);

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

    @Test
    public void writeEmptyTodoItem()  {
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

        final TodoItem todoItem = createTodoItem(createdTodo.id, "", "", "", "", false);

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


    private TodoDao getTodoDao(){
        return getTodoDatabase().todoDao();
    }

    private TodosDatabase getTodoDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                TodosDatabase.class,
                "todos_database"
        ).allowMainThreadQueries().build();
    }

    private Context getApplicationContext(){
        return InstrumentationRegistry
                .getTargetContext()
                .getApplicationContext();
    }

    private Todo createTodo(final String title){
        final Todo todo = new Todo();
        todo.title=title;
        return todo;
    }

    private void waitForTodosToBecomeThatMany(final int numberOfWishedTodos, final TodoDao todoDao){//NOPMD
        List<Todo> allTodos;
        do{
            allTodos = todoDao.getAll();
        }while (allTodos.size() != numberOfWishedTodos);
    }

    private TodoItemsDatabase getTodoItemsDatabase(){
        return Room.databaseBuilder(
                getApplicationContext(),
                TodoItemsDatabase.class,
                "todoitems_database"
        ).allowMainThreadQueries().build();
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

    private void waitForTodoItemsToBecomeThatMany(final int numberOfWishedTodoItems, final TodoItemDao todoItemDao){//NOPMD
        List<TodoItem> allTodoItems;
        do{
            allTodoItems = todoItemDao.getAll();
        }while (allTodoItems.size() != numberOfWishedTodoItems);
    }
}
