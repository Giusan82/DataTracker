package com.example.android.datatracker.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.datatracker.data.DataContract.DataEntry;
import com.example.android.datatracker.data.TasksContract.TasksEntry;

public class DataDbHelper extends SQLiteOpenHelper{
    //Name of the database file
    private static final String DATABASE_NAME = "DataTracker.db";
    //Database version.
    private static final int DATABASE_VERSION = 1;
    // this contains the SQL statement to create the table
    private static final String SQL_CREATE_TABLE_TASKS = "CREATE TABLE " + TasksEntry.TABLE_NAME + " ("
            + TasksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TasksEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + TasksEntry.COLUMN_DESCRIPTION + " TEXT, "
            + TasksEntry.COLUMN_CREATION_DATE + " REAL NOT NULL, "
            + TasksEntry.COLUMN_STARTING_TIME + " REAL, "
            + TasksEntry.COLUMN_INTERVAL + " REAL, "
            + TasksEntry.COLUMN_INTERVAL_UNIT + " INTEGER, "
            + TasksEntry.COLUMN_DURATION + " REAL, "
            + TasksEntry.COLUMN_DURATION_UNIT + " INTEGER, "
            + TasksEntry.COLUMN_TASKS_COMPLETED + " INTEGER, "
            + TasksEntry.COLUMN_ALARM_ID + " REAL, "
            + TasksEntry.COLUMN_ALARM + " INTEGER, "
            + TasksEntry.COLUMN_IMAGE + " BLOB);";

    // this contains the SQL statement to create the table
    private static final String SQL_CREATE_TABLE_DATA = "CREATE TABLE " + DataEntry.TABLE_NAME + " ("
            + DataEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataEntry.COLUMN_NAME + " TEXT NOT NULL, "
            + DataEntry.COLUMN_CREATION_DATE + " REAL NOT NULL, "
            + DataEntry.COLUMN_TASK_ID + " INTEGER NOT NULL, "
            + DataEntry.COLUMN_X + " REAL NOT NULL, "
            + DataEntry.COLUMN_Y + " REAL NOT NULL); ";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TABLE_TASKS);
        db.execSQL(SQL_CREATE_TABLE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TasksEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        onCreate(db);
    }
}
