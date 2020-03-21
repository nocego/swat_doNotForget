package ch.hslu.mobpro.donotforget;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import ch.hslu.mobpro.donotforget.todosroomdatabase.Todo;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodoDao;
import ch.hslu.mobpro.donotforget.todosroomdatabase.TodosDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class todosroomdatabaseTest {

    private final static String TEST_TITLE = "TestTitle";
    private final static String TITLE_WITH_UMLAUT = "äöüTitle";

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
}
