package com.mike.word.container.wordcontainer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;

/**
 * Implementation of App Widget functionality.
 */
public class WordWidget extends AppWidgetProvider {
    private String word;
    private String definition;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        getSharedPreferences(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.word_widget);

            if (word == null || definition == null) {
                views.setViewVisibility(R.id.no_preferences, View.VISIBLE);
                views.setTextViewText(R.id.no_preferences,
                        context.getString(R.string.no_preferences));
            } else {
                views.setViewVisibility(R.id.no_preferences, View.GONE);
                views.setTextViewText(R.id.word, word);
                views.setTextViewText(R.id.definition, definition);
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        getSharedPreferences(context);

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, this.getClass());
//            manager.notifyAppWidgetViewDataChanged(
//                    manager.getAppWidgetIds(componentName), R.layout.word_widget);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.word_widget);

            if (word == null || definition == null) {
                views.setViewVisibility(R.id.no_preferences, View.VISIBLE);
                views.setTextViewText(R.id.no_preferences,
                        context.getString(R.string.no_preferences));
            } else {
                views.setViewVisibility(R.id.no_preferences, View.GONE);
                views.setTextViewText(R.id.word, word);
                views.setTextViewText(R.id.definition, definition);
            }

            manager.updateAppWidget(componentName, views);
        }

        super.onReceive(context, intent);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, WordWidget.class));
        context.sendBroadcast(intent);
    }

    private void getSharedPreferences(Context context) {
        SharedPreferences preferences
                = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        word = preferences.getString(ConstantUtilities.SP_WORD, "");
        definition = preferences.getString(ConstantUtilities.SP_WORD_DEFINITION, "");
    }
}

