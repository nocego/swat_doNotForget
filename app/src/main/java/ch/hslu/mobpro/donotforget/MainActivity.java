package ch.hslu.mobpro.donotforget;

import android.appwidget.AppWidgetManager;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.hslu.mobpro.donotforget.notesRoomDatabase.Note;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesRoomDatabase.NotesDatabase;

public class MainActivity extends AppCompatActivity {

    private NotesDatabase notesDb;
    private List<Note> noteList;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setupViewPager();
        getDb();
        fillNoteList();
        editText = findViewById(R.id.editText);
    }

    @Override
    public void onResume(){
        super.onResume();

        int currentTabIndex;
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            currentTabIndex = intent.getExtras().getInt("currentTabIndex");
        } else {
            currentTabIndex = 0;
        }
        selectTab(currentTabIndex);
        updatePreferences();
    }

    public void openNoteDetail(View v){
        Intent intent = new Intent(MainActivity.this, NoteDetail.class);
        startActivity(intent);
    }

    public void openNoteNew(View v){
        Intent intent = new Intent(MainActivity.this, NoteNew.class);
        startActivity(intent);
    }

    public void openTodoDetail(View v){
        Intent intent = new Intent(MainActivity.this, TodoDetail.class);
        startActivity(intent);
    }

    public void openTodoNew(View v){
        Intent intent = new Intent(MainActivity.this, ToDoNew.class);
        startActivity(intent);
    }

    private void setupViewPager(){
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void selectTab(int chosenTabIndex){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        TabLayout.Tab tab = tabLayout.getTabAt(chosenTabIndex);
        tab.select();
    }

    private void getDb(){
        notesDb = Room.databaseBuilder(
                getApplicationContext(),
                NotesDatabase.class,
                "notes_database"
        ).allowMainThreadQueries().build();
    }

    private void fillNoteList(){
        NoteDao noteDao = notesDb.noteDao();
        noteList = noteDao.getAll();
    }

    public void updatePreferences(){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> notetitles = new HashSet<>();
        for(Note note: noteList){
            notetitles.add(note.title);
        }
        editor.putStringSet("text", notetitles);
        editor.apply();
        updateWidget();
    }

    private void updateWidget(){
        ComponentName widget = new ComponentName(this, DoNotForgetWidgetProvider.class);
        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(widget);

        Intent updateWidget = new Intent(this, DoNotForgetWidgetProvider.class);
        updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        this.sendBroadcast(updateWidget);
    }

}
