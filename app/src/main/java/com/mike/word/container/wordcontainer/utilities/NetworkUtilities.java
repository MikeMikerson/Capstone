package com.mike.word.container.wordcontainer.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by mike on 18/01/08.
 */

public final class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();

    private static final String BASE_URL = "https://od-api.oxforddictionaries.com/api/v1/";
    private static final String SEARCH_URL = BASE_URL + "search/en?";

    // Place the API_ID and API_KEY here
    private static final String API_ID = "";
    private static final String API_KEY = "";

    public static String getHttpResponse(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
