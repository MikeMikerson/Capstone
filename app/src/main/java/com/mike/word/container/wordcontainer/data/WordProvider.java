package com.mike.word.container.wordcontainer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mike.word.container.wordcontainer.data.WordContract.WordEntry;

/**
 * Created by mike on 18/01/08.
 */

public class WordProvider extends ContentProvider {
    private static final int WORD = 100;
    private static final int WORD_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY,
                WordContract.PATH_WORD, WORD);
        sUriMatcher.addURI(WordContract.CONTENT_AUTHORITY,
                WordContract.PATH_WORD + "/#", WORD_ID);
    }

    private WordDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new WordDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD:
                cursor = db.query(WordContract.WordEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case WORD_ID:
                selection = WordEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = db.query(WordEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD:
                return insertWord(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertWord(Uri uri, ContentValues values) {
        String wordId = values.getAsString(WordEntry.COLUMN_WORD_ID);
        if (wordId == null) {
            throw new IllegalArgumentException("Word requires a word id");
        }

        String word = values.getAsString(WordEntry.COLUMN_WORD);
        if (word == null) {
            throw new IllegalArgumentException("Word requires a word");
        }

        String definition = values.getAsString(WordEntry.COLUMN_DEFINITION);
        if (definition == null) {
            throw new IllegalArgumentException("Word requires a definition");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insert(WordEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        // Inserting and deleting favorites are sufficient for this app
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD_ID:
                selection = WordEntry.COLUMN_WORD_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(WordEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD:
                return WordEntry.CONTENT_LIST_TYPE;
            case WORD_ID:
                return WordEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    // Checks if there is currently anything in the database. Returns 0 if no data
    public static int hasWord(Context context, String wordId) {
        Cursor cursor = null;
        WordDBHelper dbHelper = new WordDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String query = "SELECT count(*) FROM " + WordEntry.TABLE_NAME
                    + " WHERE " + WordEntry.COLUMN_WORD_ID + " = ?";
            cursor = db.rawQuery(query, new String[] {wordId});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }
    }

}
