package com.mike.word.container.wordcontainer.ui;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.data.WordContract.WordEntry;
import com.mike.word.container.wordcontainer.data.WordProvider;
import com.mike.word.container.wordcontainer.listeners.OnFabTouchListener;
import com.mike.word.container.wordcontainer.models.Word;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;
import com.mike.word.container.wordcontainer.utilities.NetworkUtilities;
import com.mike.word.container.wordcontainer.utilities.WordJsonUtilities;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordDetailsActivity extends AppCompatActivity {
    @BindView(R.id.word) TextView wordView;
    @BindView(R.id.definition) TextView definitionView;
    @BindView(R.id.example) TextView exampleView;
    @BindView(R.id.add_to_favorites) FloatingActionButton fabView;

    private String wordId;
    private String selectedWord;
    private String wordRegion;
    private Toast toast;

    private final int FIRST_ELEMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        ButterKnife.bind(this);

        getWordIdFromIntentExtras();
        setSelectedWord();
        executeAsyncTask(wordId);
    }

    private void getWordIdFromIntentExtras() {
        wordId = getIntent().getStringExtra(ConstantUtilities.WORD_ID);
        selectedWord = getIntent().getStringExtra(ConstantUtilities.SEARCH_WORD);
        wordRegion = getIntent().getStringExtra(ConstantUtilities.WORD_REGION);
    }

    private void setSelectedWord() {
        wordView.setText(selectedWord);
    }

    private void executeAsyncTask(String wordId) {
        WordDetailsAsyncTask task = new WordDetailsAsyncTask(new OnFabTouchListener() {
            @Override
            public void onTouch(Word word) {
                saveWordAsFavorite(word);
            }
        });
        task.execute(wordId);
    }

    private void saveWordAsFavorite(Word word) {
        ContentValues values = new ContentValues();

        values.put(WordEntry.COLUMN_WORD_ID, word.getId());
        values.put(WordEntry.COLUMN_WORD, selectedWord);
        values.put(WordEntry.COLUMN_DEFINITION, word.getDefinition());

        if (word.getExampleList() != null && word.getExampleList().size() > 0) {
            values.put(WordEntry.COLUMN_EXAMPLE_LIST, word.getExampleList().get(FIRST_ELEMENT));
        }

        Uri newUri = null;
        if (WordProvider.hasWord(getBaseContext(), word.getId()) == 0) {
            newUri = getContentResolver().insert(WordEntry.CONTENT_URI, values);
        }

        if (newUri == null) {
            showToast(getString(R.string.already_favorite));
        } else {
            showToast(getString(R.string.save_successful));
        }
    }

    public void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        }

        if (toast.getView().isShown()) {
            toast.cancel();
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class WordDetailsAsyncTask extends AsyncTask<String, Void, Word> {
        private OnFabTouchListener listener;

        public WordDetailsAsyncTask(OnFabTouchListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPostExecute(final Word word) {

            definitionView.setText(word.getDefinition());
            if (word.getExampleList() != null && word.getExampleList().size() > 0) {
                exampleView.setText(word.getExampleList().get(FIRST_ELEMENT));
            }

            fabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onTouch(word);
                }
            });
        }

        @Override
        protected Word doInBackground(String... strings) {
            if (strings.length < 1 || strings[0] == null || strings[0].length() < 1) return null;
            URL detailsUrl = NetworkUtilities.getWordDetails(strings[0]);

            try {
                String jsonResponse = NetworkUtilities.getHttpResponse(detailsUrl);

                return WordJsonUtilities.getWordDetails(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
