package com.mike.word.container.wordcontainer.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
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

    private String wordId;
    private String selectedWord;
    private String wordRegion;

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
        WordDetailsAsyncTask task = new WordDetailsAsyncTask();
        task.execute(wordId);
    }

    private class WordDetailsAsyncTask extends AsyncTask<String, Void, Word> {
        @Override
        protected void onPostExecute(Word word) {

            definitionView.setText(word.getDefinition());

            // TODO: Make into a list with all listed examples
            if (word.getExampleList() != null) {
                for (String example : word.getExampleList()) {
                    exampleView.setText(example);
                }
            }
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
