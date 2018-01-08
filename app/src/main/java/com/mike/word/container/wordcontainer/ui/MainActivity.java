package com.mike.word.container.wordcontainer.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;
import com.mike.word.container.wordcontainer.utilities.NetworkUtilities;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.search_word) EditText searchWord;
    @BindView(R.id.display_favorites) Button getFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setEditTextListener();
    }

    private void executeAsyncTask(String userWord) {
        WordAsyncTask task = new WordAsyncTask();
        task.execute(userWord);
    }

    private String setEditTextListener() {
        searchWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String userWord = textView.getText().toString();
                    executeAsyncTask(userWord);
                }
                return true;
            }
        });
        return searchWord.getText().toString();
    }

    private class WordAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String data) {
            if (data == null) return;

            Log.i("onPostExecute", data);
            Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
            intent.putExtra(ConstantUtilities.WORD_LIST, data);
            startActivity(intent);
        }

        @Override
        protected String doInBackground(String... word) {
            if (word.length < 1 || word[0] == null || word[0].length() < 1) return null;
            URL searchUrl = NetworkUtilities.getSearchResults(word[0]);

            try {
                String searchResponse = NetworkUtilities.getHttpResponse(searchUrl);

                // TODO: Convert to list of Words
                return searchResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
