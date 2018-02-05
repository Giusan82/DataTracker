package com.example.android.datatracker.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class TasksContract {
    //To prevent someone from accidentally instantiating the contract class
    private TasksContract() {
    }

    //This is the name of the entire content provider expressed as package name
    public static final String CONTENT_AUTHORITY = "com.example.android.datatracker";
    //Base URI used to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Path appended to base content URI referred to the items table
    public static final String PATH_TASKS = "tasks";

    //Inner class that defines constant values for the items database table.
    //Each entry in the table represents a single item.
    public static final class TasksEntry implements BaseColumns {
        //The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_DATA_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
        //Name of database table
        public static final String TABLE_NAME = "tasks";
        //Unique ID number for the item (only for use in the database table). Type: INTEGER
        public static final String ID = BaseColumns._ID;
        // Name of the task. Type: TEXT
        public static final String COLUMN_NAME = "name";
        // Name of the task. Type: TEXT
        public static final String COLUMN_DESCRIPTION = "description";
        //item's date. Type: REAL
        public static final String COLUMN_CREATION_DATE = "creation_date";
        public static final String COLUMN_STARTING_TIME = "starting_time";
        public static final String COLUMN_INTERVAL = "interval";
        public static final String COLUMN_DURATION = "duration";
        //item's date. Type: INTEGER
        public static final String COLUMN_TASKS_COMPLETED = "tasks_completed";
        //item's date. Type: REAL
        public static final String COLUMN_INTERVAL_UNIT = "interval_unit";
        //item's date. Type: REAL
        public static final String COLUMN_DURATION_UNIT = "duration_unit";

        public static final long SECOND = 1000;
        public static final long MINUTE = 60 * SECOND;
        public static final long HOUR = 60 * MINUTE;
        public static final long DAY = 24 * HOUR;
        public static final long WEEK = 7 * DAY;
        public static final String COLUMN_ALARM_ID = "alarm_id";
        //item's alarm. Type: INTEGER, 0 is off 1 is on
        public static final String COLUMN_ALARM = "alarm";
        //item's image. Type: BLOB
        public static final String COLUMN_IMAGE = "image";
    }
}
