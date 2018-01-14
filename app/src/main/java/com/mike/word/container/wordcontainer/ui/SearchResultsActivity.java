package com.mike.word.container.wordcontainer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.adapter.WordSearchAdapter;
import com.mike.word.container.wordcontainer.listeners.OnWordClickListener;
import com.mike.word.container.wordcontainer.models.Word;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity {
    @BindView(R.id.search_results_recycler_view) RecyclerView recyclerView;

    private WordSearchAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Word> wordList;
    private String searchWord;

    private boolean isFavorite = false;

    private static final int CACHE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ButterKnife.bind(this);

        getIntentExtras();

        if (searchWord != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(searchWord);
        }

        setViewLayoutManager();
        setRecyclerViewOptions();
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (getIntent().hasExtra(ConstantUtilities.WORD_LIST)) {
                wordList = bundle.getParcelableArrayList(ConstantUtilities.WORD_LIST);
                searchWord = getIntent().getStringExtra(ConstantUtilities.SEARCH_WORD);
            } else {
                wordList = bundle.getParcelableArrayList(ConstantUtilities.WORD_LIST_FAVORITE);
                isFavorite = true;
            }
        }
    }

    private void setViewLayoutManager() {
        layoutManager = new LinearLayoutManager(this);
    }

    private void setRecyclerViewOptions() {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(CACHE_SIZE);
        recyclerView.setDrawingCacheEnabled(true);

        DividerItemDecoration decoration = new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        adapter = new WordSearchAdapter(this, wordList, new OnWordClickListener() {
            @Override
            public void onWordClick(Word word, View wordView) {
                Intent intent = new Intent(
                        SearchResultsActivity.this, WordDetailsActivity.class);
                intent.putExtra(ConstantUtilities.WORD_ID, word.getId());
                intent.putExtra(ConstantUtilities.SEARCH_WORD, word.getWord());

                intent.putExtra(getString(R.string.list_to_details_transition), word.getWord());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                SearchResultsActivity.this,
                                wordView,
                                getString(R.string.list_to_details_transition));

                if (isFavorite) {
                    ArrayList<Word> arrayList = new ArrayList<>();
                    arrayList.addAll(wordList);

                    intent.putExtra(ConstantUtilities.WORD_FAVORITE, word);
                    intent.putExtra(ConstantUtilities.IS_FAVORITE, isFavorite);
                }

                startActivity(intent, optionsCompat.toBundle());
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
