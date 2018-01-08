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
import com.mike.word.container.wordcontainer.models.Word;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;
import com.mike.word.container.wordcontainer.utilities.NetworkUtilities;
import com.mike.word.container.wordcontainer.utilities.WordJsonUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
}
