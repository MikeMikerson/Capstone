package com.mike.word.container.wordcontainer.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.mike.word.container.wordcontainer.widget.WordWidget;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordDetailsActivity extends AppCompatActivity {
    @BindView(R.id.word) TextView wordView;
    @BindView(R.id.definition) TextView definitionView;
    @BindView(R.id.example) TextView exampleView;
    @BindView(R.id.definition_title) TextView definitionTitleView;
    @BindView(R.id.example_title) TextView exampleTitleView;
    @BindView(R.id.no_definition) TextView noDefinitionView;
    @BindView(R.id.add_to_favorites) FloatingActionButton fabView;

    private String wordId;
    private String selectedWord;
    private String wordDefinition;
    private String wordExample;
    private boolean isFavorite = false;
    private Toast toast;
    private Word favoriteWord;
    private MenuItem menuItem;
    private boolean menuVisibility = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            wordId = savedInstanceState.getString(ConstantUtilities.DETAILS_ID);
            selectedWord = savedInstanceState.getString(ConstantUtilities.DETAILS_WORD);
            wordExample = savedInstanceState.getString(ConstantUtilities.DETAILS_EXAMPLE);
            wordDefinition = savedInstanceState.getString(ConstantUtilities.DETAILS_DEFINITION);
            isFavorite = savedInstanceState.getBoolean(ConstantUtilities.DETAILS_IS_FAVORITE);
            menuVisibility =
                    savedInstanceState.getBoolean(ConstantUtilities.DETAILS_MENU_VISIBILITY);

            setupView(savedInstanceState);
        }

        getWordIdFromIntentExtras();
        setSelectedWord();
        setTitle();

        if (!isFavorite && savedInstanceState == null) {
            executeAsyncTask(wordId);
        } else if (savedInstanceState == null) {
            displayFavorite();
        }

        if (isFavorite) {
            fabView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_widget_button, menu);

        menuItem = menu.findItem(R.id.add_widget);
        menuItem.setVisible(menuVisibility);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_widget:
                displayAddWidgetDialogue();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(ConstantUtilities.DETAILS_ID, wordId);
        outState.putString(ConstantUtilities.DETAILS_WORD, selectedWord);
        outState.putBoolean(ConstantUtilities.DETAILS_IS_FAVORITE, isFavorite);
        outState.putInt(ConstantUtilities.DETAILS_FAB_VISIBILITY, fabView.getVisibility());
        outState.putBoolean(ConstantUtilities.DETAILS_MENU_VISIBILITY, menuItem.isVisible());

        if (wordDefinition != null) {
            outState.putString(ConstantUtilities.DETAILS_DEFINITION, wordDefinition);
        }

        if (wordExample != null) {
            outState.putString(ConstantUtilities.DETAILS_EXAMPLE, wordExample);
        }
    }

    private void setTitle() {
        String title = String.format(
                (getResources().getString(R.string.details_title)),
                selectedWord);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void displayAddWidgetDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.dialogue_title));
        builder.setMessage(getString(R.string.diaglogue_message));

        builder.setPositiveButton(getString(R.string.dialogue_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveWordToPreferences();
                Toast.makeText(WordDetailsActivity.this,
                        getString(R.string.dialogue_widget_added), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(getString(R.string.dialogue_cancel), null);

        builder.show();
    }

    private void getWordIdFromIntentExtras() {
        wordId = getIntent().getStringExtra(ConstantUtilities.WORD_ID);
        selectedWord = getIntent().getStringExtra(ConstantUtilities.SEARCH_WORD);

        if (getIntent().hasExtra(ConstantUtilities.IS_FAVORITE)) {
            isFavorite = true;
            favoriteWord = getIntent().getParcelableExtra(ConstantUtilities.WORD_FAVORITE);
        }
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

        if (NetworkUtilities.hasConnection(this)) {
            task.execute(wordId);
        } else {
            Toast.makeText(this,
                    getString(R.string.no_internet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayFavorite() {
        wordDefinition = favoriteWord.getDefinition();

        definitionView.setText(favoriteWord.getDefinition());
        if (favoriteWord.getExample() != null) {
            exampleView.setText(favoriteWord.getExample());
        } else {
            exampleTitleView.setVisibility(View.GONE);
        }
    }

    private void saveWordAsFavorite(Word word) {
        ContentValues values = new ContentValues();

        values.put(WordEntry.COLUMN_WORD_ID, word.getId());
        values.put(WordEntry.COLUMN_WORD, selectedWord);

        if (word.getDefinition() != null) {
            values.put(WordEntry.COLUMN_DEFINITION, wordDefinition);
        }

        if (word.getExample() != null) {
            values.put(WordEntry.COLUMN_EXAMPLE_LIST, word.getExample());
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

    // To use in home screen widget
    private void saveWordToPreferences() {
        if (selectedWord == null || wordDefinition == null) return;

        SharedPreferences preferences
                = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ConstantUtilities.SP_WORD, selectedWord);
        editor.putString(ConstantUtilities.SP_WORD_DEFINITION, wordDefinition);
        editor.apply();

        WordWidget.sendRefreshBroadcast(this);
    }

    private void showToast(String message) {
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

    private void setupView(Bundle savedInstanceState) {
        if (wordDefinition != null ) {
            definitionView.setText(wordDefinition);
        } else {
            definitionTitleView.setVisibility(View.GONE);
            definitionView.setVisibility(View.GONE);
            noDefinitionView.setVisibility(View.VISIBLE);
            fabView.setVisibility(View.GONE);
        }

        // Only using one example - the first one - in this app.
        if (wordExample != null) {
            exampleView.setText(wordExample);
        } else {
            exampleTitleView.setVisibility(View.GONE);
        }

        fabView.setVisibility(savedInstanceState.getInt(ConstantUtilities.DETAILS_FAB_VISIBILITY));
    }

    private class WordDetailsAsyncTask extends AsyncTask<String, Void, Word> {
        private OnFabTouchListener listener;

        public WordDetailsAsyncTask(OnFabTouchListener listener) {
            this.listener = listener;
        }

        @Override
        protected void onPostExecute(final Word word) {
            if (word == null) {
                definitionTitleView.setVisibility(View.GONE);
                definitionView.setVisibility(View.GONE);
                noDefinitionView.setVisibility(View.VISIBLE);
                fabView.setVisibility(View.GONE);
                exampleTitleView.setVisibility(View.GONE);
                menuItem.setVisible(false);
                return;
            }

            // Some words don't have any definitions
            if (word.getDefinition() != null ) {
                wordDefinition = word.getDefinition();
                definitionView.setText(wordDefinition);
            } else {
                menuItem.setVisible(false);
                definitionTitleView.setVisibility(View.GONE);
                definitionView.setVisibility(View.GONE);
                noDefinitionView.setVisibility(View.VISIBLE);
                fabView.setVisibility(View.GONE);
            }

            if (word.getExample() != null) {
                wordExample = word.getExample();
                exampleView.setText(wordExample);
            } else {
                exampleTitleView.setVisibility(View.GONE);
            }

            if (fabView.getVisibility() != View.GONE) {
                fabView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onTouch(word);
                    }
                });
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
