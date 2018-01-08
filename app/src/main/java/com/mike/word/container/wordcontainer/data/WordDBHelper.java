package com.mike.word.container.wordcontainer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.mike.word.container.wordcontainer.data.WordContract.WordEntry;

/**
 * Created by mike on 18/01/08.
 */

public class WordDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "wordcontainer.db";
    private static final int DATABASE_VERSION = 1;

    public static final String[] WORD_PROJECTION = {
            WordEntry._ID,
            WordEntry.COLUMN_WORD_ID,
            WordEntry.COLUMN_WORD,
            WordEntry.COLUMN_MATCH_TYPE,
            WordEntry.COLUMN_REGION,
            WordEntry.COLUMN_DEFINITION,
            WordEntry.COLUMN_EXAMPLE_LIST
    };

    public WordDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WORD_TABLE =
                "CREATE TABLE " + WordEntry.TABLE_NAME + " ("
                        + WordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + WordEntry.COLUMN_WORD_ID + " TEXT NOT NULL, "
                        + WordEntry.COLUMN_WORD + " TEXT NOT NULL, "
                        + WordEntry.COLUMN_MATCH_TYPE + " TEXT, "
                        + WordEntry.COLUMN_REGION + " TEXT, "
                        + WordEntry.COLUMN_DEFINITION + " TEXT NOT NULL, "
                        + WordEntry.COLUMN_EXAMPLE_LIST + " TEXT);";

        db.execSQL(SQL_CREATE_WORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Only change when the version of database changes
    }
}
