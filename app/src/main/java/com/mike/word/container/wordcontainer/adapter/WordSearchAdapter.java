package com.mike.word.container.wordcontainer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.listeners.OnWordClickListener;
import com.mike.word.container.wordcontainer.models.Word;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mike on 18/01/08.
 */

public class WordSearchAdapter extends RecyclerView.Adapter<WordSearchAdapter.ViewHolder> {
    private Context context;
    private List<Word> wordList;
    private OnWordClickListener listener;

    public WordSearchAdapter(Context context, List<Word> wordList, OnWordClickListener listener) {
        this.context = context;
        this.wordList = wordList;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word) TextView wordView;
        @BindView(R.id.word_list_item) LinearLayout wordItem;
        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

        public void bind(final Word word, final OnWordClickListener listener) {
            wordItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onWordClick(word, wordView);
                }
            });
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
        Word wordObj = wordList.get(position);
        String word = wordObj.getWord();

        holder.wordView.setText(word);
        holder.bind(wordObj, listener);
    }
}
