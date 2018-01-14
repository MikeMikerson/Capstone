package com.mike.word.container.wordcontainer.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mike.word.container.wordcontainer.BuildConfig;
import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.analytics.AnalyticsApplication;
import com.mike.word.container.wordcontainer.data.WordContract.WordEntry;
import com.mike.word.container.wordcontainer.data.WordDBHelper;
import com.mike.word.container.wordcontainer.models.Word;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;
import com.mike.word.container.wordcontainer.utilities.NetworkUtilities;
import com.mike.word.container.wordcontainer.utilities.WordJsonUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.search_word) EditText searchWordView;
    @BindView(R.id.display_favorites) Button getFavoritesButton;
    @BindView(R.id.ad_view) AdView adView;

    private static final int ID_FAVORITE_WORD_LOADER = 500;

    private Tracker tracker;
    private String searchWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Uncomment the following line to delete DB if something goes wrong
//        deleteDatabase();

        if (savedInstanceState != null) {
            searchWord = savedInstanceState.getString(ConstantUtilities.EDIT_SEARCH_WORD);
        }

        initializeAdMob();
        initializeAnalytics();

        setEditTextListener();
        setFavoriteButtonListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        trackScreenName();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ConstantUtilities.EDIT_SEARCH_WORD, searchWordView.getText().toString());
    }

    private void executeAsyncTask(String userWord) {
        WordAsyncTask task = new WordAsyncTask();
        task.execute(userWord);
    }

    private String setEditTextListener() {
        searchWordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    searchWord = textView.getText().toString();
                    executeAsyncTask(searchWord);
                }
                return true;
            }
        });
        return searchWordView.getText().toString();
    }

    private void setFavoriteButtonListener() {
        getFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFavoriteWords();
            }
        });
    }

    private void loadFavoriteWords() {
        getSupportLoaderManager().initLoader(ID_FAVORITE_WORD_LOADER, null, this)
                .forceLoad();
    }

    private void deleteDatabase() {
        getApplicationContext().deleteDatabase(WordDBHelper.DATABASE_NAME);
    }

    private void initializeAdMob() {
        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initializeAnalytics() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();

        Log.i(TAG, "Setting screen name");
        tracker = application.getDefaultTracker();
    }

    private void trackScreenName() {
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private class WordAsyncTask extends AsyncTask<String, Void, List<Word>> {
        @Override
        protected void onPostExecute(List<Word> wordList) {
            if (wordList == null) return;

            // In order to put as parcelable ArrayList, create a new ArrayList
            // and add all the List of words to it
            ArrayList<Word> arrayWordList = new ArrayList<>();
            arrayWordList.addAll(wordList);

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(ConstantUtilities.WORD_LIST, arrayWordList);

            Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
            intent.putExtras(bundle);
            intent.putExtra(ConstantUtilities.SEARCH_WORD, searchWord);
            startActivity(intent);
        }

        @Override
        protected List<Word> doInBackground(String... word) {
            if (word.length < 1 || word[0] == null || word[0].length() < 1) return null;
            URL searchUrl = NetworkUtilities.getSearchResults(word[0]);

            try {
                String jsonResponse = NetworkUtilities.getHttpResponse(searchUrl);
                List<Word> wordList;
                wordList = WordJsonUtilities.getWordStringsFromJson(jsonResponse);

                return wordList;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // Loader for favorites when the favorite button is clicked
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Only one loader, so don't need to check id
        return new CursorLoader(this,
                WordEntry.CONTENT_URI,
                WordDBHelper.WORD_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO: Add toast saying there are no favorites to be displayed
        if (data == null) {
            Toast.makeText(this,
                    getString(R.string.no_favorites_available),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // In order to put as parcelable ArrayList, create a new ArrayList
        // and add all the List of words to it
        ArrayList<Word> arrayWordList = new ArrayList<>();
        arrayWordList.addAll(convertCursorToMovie(data));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ConstantUtilities.WORD_LIST_FAVORITE, arrayWordList);

        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    // Convert in order to be able to reuse WordSearchAdapter
    private List<Word> convertCursorToMovie(Cursor cursor) {
        List<Word> wordList = new ArrayList<>();

        try {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                int cursorWordId = cursor.getColumnIndex(WordEntry.COLUMN_WORD_ID);
                int cursorWord = cursor.getColumnIndex(WordEntry.COLUMN_WORD);
                int cursorDefinition = cursor.getColumnIndex(WordEntry.COLUMN_DEFINITION);
                int cursorExample = cursor.getColumnIndex(WordEntry.COLUMN_EXAMPLE_LIST);

                String wordId = cursor.getString(cursorWordId);
                String word = cursor.getString(cursorWord);
                String definition = cursor.getString(cursorDefinition);
                String example = cursor.getString(cursorExample);

                // TODO: Add example
                wordList.add(new Word(wordId, word, definition, example));
            }
        } finally {
            cursor.close();
        }

        return wordList;
    }
}
