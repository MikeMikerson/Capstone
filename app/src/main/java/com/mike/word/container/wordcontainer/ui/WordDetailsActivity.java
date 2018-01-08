package com.mike.word.container.wordcontainer.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mike.word.container.wordcontainer.R;
import com.mike.word.container.wordcontainer.utilities.ConstantUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WordDetailsActivity extends AppCompatActivity {
    @BindView(R.id.word) TextView wordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        ButterKnife.bind(this);
        wordView.setText(getIntent().getStringExtra(ConstantUtilities.WORD_ID));
    }
}
