package com.mike.word.container.wordcontainer.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.mike.word.container.wordcontainer.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mike on 18/01/08.
 */

public final class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();

    private static final String BASE_URL = "https://od-api.oxforddictionaries.com/api/v1/";
    private static final String BASE_DETAILS_URL
            = "https://od-api.oxforddictionaries.com/api/v1/";
    private static final String SEARCH_URL = BASE_URL + "search/en?prefix=true&";
    private static final String DETAILS_URL = BASE_DETAILS_URL + "entries/en/";

    // Place the API_ID and API_KEY here
    private static final String APP_ID = BuildConfig.APP_ID;
    private static final String APP_KEY = BuildConfig.APP_KEY;

    // Query parameters
    private static final String API_QUERRY = "q";
    private static final String API_LIMIT = "limit";
    private static final String LIMIT = "5";

    public static URL getSearchResults(String searchWord) {
        Uri builtUri = Uri.parse(SEARCH_URL).buildUpon()
                .appendQueryParameter(API_QUERRY, searchWord)
                .appendQueryParameter(API_LIMIT, LIMIT)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL getWordDetails(String wordId) {
        Uri builtUri = Uri.parse(DETAILS_URL + wordId).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("app_id", APP_ID);
        connection.setRequestProperty("app_key", APP_KEY);

        try {
            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            return scanner.hasNext() ? scanner.next() : null;
        } finally {
            connection.disconnect();
        }
    }

    public static boolean hasConnection(Activity context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
