package com.mike.word.container.wordcontainer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.models.Word;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mike on 18/01/08.
 */

public class WordSearchAdapter extends RecyclerView.Adapter<WordSearchAdapter.ViewHolder> {
    private Context context;
    private List<Word> wordList;

    public WordSearchAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word) TextView word;
        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.search_word_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (wordList == null) return 0;

        return wordList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public void updateWordList(List<Word> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
    }
}
