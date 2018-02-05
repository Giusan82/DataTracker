package com.example.android.datatracker.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    //To prevent someone from accidentally instantiating the contract class
    private DataContract() {
    }

    //This is the name of the entire content provider expressed as package name
    public static final String CONTENT_AUTHORITY = "com.example.android.datatracker";
    //Base URI used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Path appended to base content URI referred to the items table
    public static final String PATH_DATA = "data";

    public static final class DataEntry implements BaseColumns {
        //The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DATA);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_DATA_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DATA;
        //Name of database table
        public static final String TABLE_NAME = "data";
        //Unique ID number for the item (only for use in the database table). Type: INTEGER
        public static final String ID = BaseColumns._ID;
        // Name of the task. Type: TEXT
        public static final String COLUMN_NAME = "name";
        //item's date. Type: REAL
        public static final String COLUMN_CREATION_DATE = "creation_date";

        //item's date. Type: INTEGER
        public static final String COLUMN_TASK_ID = "task_id";

        //item's x value. Type: REAL
        public static final String COLUMN_X = "X";
        //item's y value. Type: REAL
        public static final String COLUMN_Y = "Y";
    }
}
