package com.example.android.datatracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.datatracker.data.TasksContract.TasksEntry;
import com.example.android.datatracker.data.DataContract.DataEntry;
import com.example.android.datatracker.data.LabelsContract.LabelsEntry;


public class DataProvider extends ContentProvider {
    //Tag for the log messages
    public static final String LOG_TAG = DataProvider.class.getSimpleName();
    //URI matcher code for the content URI for the tasks table
    public static final int TASKS = 100;
    //URI matcher code for a single task
    public static final int TASK_ID = 101;

    //URI matcher code for the content URI for the tasks table
    public static final int DATA = 200;
    //URI matcher code for a single task
    public static final int DATA_ID = 201;

    //URI matcher code for the content URI for the tasks table
    public static final int LABELS = 300;
    //URI matcher code for a single task
    public static final int LABELS_ID = 301;
    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer for sUriMatcher
    static {
        sUriMatcher.addURI(TasksContract.CONTENT_AUTHORITY, TasksContract.PATH_TASKS, TASKS);
        sUriMatcher.addURI(TasksContract.CONTENT_AUTHORITY, TasksContract.PATH_TASKS + "/#", TASK_ID);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_DATA, DATA);
        sUriMatcher.addURI(DataContract.CONTENT_AUTHORITY, DataContract.PATH_DATA + "/#", DATA_ID);
        sUriMatcher.addURI(LabelsContract.CONTENT_AUTHORITY, LabelsContract.PATH_LABELS, LABELS);
        sUriMatcher.addURI(LabelsContract.CONTENT_AUTHORITY, LabelsContract.PATH_LABELS + "/#", LABELS_ID);
    }

    //initialize a DataDbHelper object
    private DataDbHelper dataDbHelper;
    private Cursor mCursor;

    @Override
    public boolean onCreate() {
        dataDbHelper = new DataDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase db = dataDbHelper.getReadableDatabase();

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // This queries the values table directly with the given
                // projection, selection, selection arguments, and sort order.
                mCursor = db.query(TasksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASK_ID:
                // This extracts out the ID from the URI and queries the table at specific id
                selection = TasksContract.TasksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query at specific id
                mCursor = db.query(TasksContract.TasksEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DATA:
                // This queries the values table directly with the given
                // projection, selection, selection arguments, and sort order.
                mCursor = db.query(DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DATA_ID:
                // This extracts out the ID from the URI and queries the table at specific id
                selection = DataEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query at specific id
                mCursor = db.query(DataEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LABELS:
                // This queries the values table directly with the given
                // projection, selection, selection arguments, and sort order.
                mCursor = db.query(LabelsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case LABELS_ID:
                // This extracts out the ID from the URI and queries the table at specific id
                selection = LabelsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query at specific id
                mCursor = db.query(LabelsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //set notification URI on the Cursor. If the data at this URI changes, it knows where need to update the Cursor.
        mCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return mCursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return insertValue(uri, contentValues, TasksEntry.COLUMN_NAME, TasksEntry.TABLE_NAME);
            case DATA:
                return insertValue(uri, contentValues, DataEntry.COLUMN_NAME, DataEntry.TABLE_NAME);
            case LABELS:
                return insertValue(uri, contentValues, LabelsEntry.COLUMN_NAME, LabelsEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a new task into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertValue(Uri uri, ContentValues values, String column_name, String table_name) {
        // Check that the name is not null
        String name = values.getAsString(column_name);
        if (name == null) {
            throw new IllegalArgumentException("Task requires a name");
        }
        SQLiteDatabase database = dataDbHelper.getWritableDatabase();
//        if(mCursor.getCount() == 0){
//            //reset the entry count in sqlite_sequence table
//            database.execSQL("delete from sqlite_sequence where name='" + TasksEntry.TABLE_NAME + "'");
//        }
        // Insert the new item with the given values
        long id = database.insert(table_name, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //return the new URI with the ID append at the end
        // Notify all listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return updateItem(uri, contentValues, selection, selectionArgs, TasksEntry.TABLE_NAME, TasksEntry.COLUMN_NAME);
            case TASK_ID:
                // updates the table at specific id
                selection = TasksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs, TasksEntry.TABLE_NAME, TasksEntry.COLUMN_NAME);
            case DATA:
                return updateItem(uri, contentValues, selection, selectionArgs, DataEntry.TABLE_NAME, DataEntry.COLUMN_NAME);
            case DATA_ID:
                // updates the table at specific id
                selection = DataEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs, DataEntry.TABLE_NAME, DataEntry.COLUMN_NAME);
            case LABELS:
                return updateItem(uri, contentValues, selection, selectionArgs, LabelsEntry.TABLE_NAME, LabelsEntry.COLUMN_NAME);
            case LABELS_ID:
                // updates the table at specific id
                selection = LabelsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs, LabelsEntry.TABLE_NAME, LabelsEntry.COLUMN_NAME);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update the database with the given content values.
     */
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs, String table_name, String column_name) {
        /** If the {@link column_name} key is present, check that the name value is not null.*/
        if (values.containsKey(column_name)) {
            // Check that the name is not null
            String name = values.getAsString(column_name);
            if (name == null) {
                throw new IllegalArgumentException("Task requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = dataDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(table_name, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //Return the number of rows that were affected
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase db = dataDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(TasksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                // Delete a single row given by the ID in the URI
                selection = TasksEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TasksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DATA_ID:
                // Delete a single row given by the ID in the URI
                selection = DataEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(DataEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LABELS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(LabelsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LABELS_ID:
                // Delete a single row given by the ID in the URI
                selection = LabelsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(LabelsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return TasksEntry.CONTENT_DATA_TYPE;
            case DATA:
                return DataEntry.CONTENT_LIST_TYPE;
            case DATA_ID:
                return DataEntry.CONTENT_DATA_TYPE;
            case LABELS:
                return LabelsEntry.CONTENT_LIST_TYPE;
            case LABELS_ID:
                return LabelsEntry.CONTENT_DATA_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
