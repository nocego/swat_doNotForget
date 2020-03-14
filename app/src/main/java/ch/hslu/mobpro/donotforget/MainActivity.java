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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.hslu.mobpro.donotforget.notesroomdatabase.Note;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NoteDao;
import ch.hslu.mobpro.donotforget.notesroomdatabase.NotesDatabase;

public class MainActivity extends AppCompatActivity {//NOPMD

    private NotesDatabase notesDb;
    private List<Note> noteList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {//NOPMD
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hide action bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setupViewPager();
        getDb();
        fillNoteList();
    }

    @Override
    public void onResume(){
        super.onResume();

        int currentTabIndex;
        final Intent intent = getIntent();
        if (intent.getExtras() == null) {
            currentTabIndex = 0;
        } else {
            currentTabIndex = intent.getExtras().getInt("currentTabIndex");
        }
        selectTab(currentTabIndex);
        updatePreferences();
    }

    public void openNoteDetail(final View v){//NOPMD
        final Intent intent = new Intent(MainActivity.this, NoteDetail.class);
        startActivity(intent);
    }

    public void openNoteNew(final View v){//NOPMD
        final Intent intent = new Intent(MainActivity.this, NoteNew.class);
        startActivity(intent);
    }

    public void openTodoDetail(final View v){//NOPMD
        final Intent intent = new Intent(MainActivity.this, TodoDetail.class);
        startActivity(intent);
    }

    public void openTodoNew(final View v){//NOPMD
        final Intent intent = new Intent(MainActivity.this, ToDoNew.class);
        startActivity(intent);
    }

    private void setupViewPager(){
        final ViewPager viewPager = findViewById(R.id.viewPager);

        final SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void selectTab(final int chosenTabIndex){
        final TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        final TabLayout.Tab tab = tabLayout.getTabAt(chosenTabIndex);
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
        final NoteDao noteDao = notesDb.noteDao();
        noteList = noteDao.getAll();
    }

    public void updatePreferences(){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        final Set<String> notetitles = new HashSet<>();
        for(final Note note: noteList){
            notetitles.add(note.title);
        }
        editor.putStringSet("text", notetitles);
        editor.apply();
        updateWidget();
    }

    private void updateWidget(){
        final ComponentName widget = new ComponentName(this, DoNotForgetWidgetProvider.class);
        final int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(widget);

        final Intent updateWidget = new Intent(this, DoNotForgetWidgetProvider.class);
        updateWidget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        this.sendBroadcast(updateWidget);
    }

}
