package com.mike.word.container.wordcontainer.analytics;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mike.word.container.wordcontainer.R;

/**
 * Created by mike on 18/01/12.
 */

public class AnalyticsApplication extends Application {
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        sAnalytics = GoogleAnalytics.getInstance(this);
    }

    synchronized public Tracker getDefaultTracker() {
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(getString(R.string.ga_trackingId));
        }

        return sTracker;
    }
}
