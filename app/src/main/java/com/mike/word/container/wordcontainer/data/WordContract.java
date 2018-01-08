package com.mike.word.container.wordcontainer.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mike on 18/01/08.
 */

public class WordContract {

    // Prevent instantiating class
    private WordContract() {
        throw new AssertionError();
    }

    public static final String CONTENT_AUTHORITY = "com.mike.word.container.wordcontainer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WORD = "wordcontainer";

    public static final class WordEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WORD);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORD;


        public static final String TABLE_NAME = "word";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_WORD_ID = "word_id";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_MATCH_TYPE = "matchType";
        public static final String COLUMN_REGION = "region";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_EXAMPLE_LIST = "example_list";
    }
}
