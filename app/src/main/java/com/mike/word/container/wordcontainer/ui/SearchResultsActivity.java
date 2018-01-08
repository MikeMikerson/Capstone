package com.mike.word.container.wordcontainer.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.adapter.WordSearchAdapter;
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

    private static final int CACHE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ButterKnife.bind(this);

        getIntentExtras();

        setViewLayoutManager();
        setRecyclerViewOptions();
    }

    private void getIntentExtras() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            wordList = bundle.getParcelableArrayList(ConstantUtilities.WORD_LIST);
        }
    }

    private void setViewLayoutManager() {
        layoutManager = new LinearLayoutManager(this);
    }

    private void setRecyclerViewOptions() {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(CACHE_SIZE);
        recyclerView.setDrawingCacheEnabled(true);

        adapter = new WordSearchAdapter(this, wordList);
        recyclerView.setAdapter(adapter);
    }
}
