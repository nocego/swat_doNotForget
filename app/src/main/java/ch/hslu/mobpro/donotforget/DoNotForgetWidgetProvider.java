package ch.hslu.mobpro.donotforget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class DoNotForgetWidgetProvider extends AppWidgetProvider {
    private static final String WIDGET_ON_CLICK = "myOnClickTag";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> values = preferences.getStringSet("text", new HashSet<String>());

        StringBuilder sbld = new StringBuilder();
        for (String value: values){
            sbld.append(value);
            sbld.append("\n");
        }
        String val = sbld.toString();

        //perform this loop for each App Widget that belongs this provider
        for (final int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.donotforget_widget_provider);
            views.setTextViewText(R.id.appwidget_text, val);
            views.setOnClickPendingIntent(R.id.button22,getPendingSelfIntent(context, WIDGET_ON_CLICK));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        if (WIDGET_ON_CLICK.equals(intent.getAction())){
            Intent startIntent = new Intent(context, MainActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        }
    }
}

