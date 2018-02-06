package com.example.android.datatracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.datatracker.data.TasksContract.TasksEntry;
import com.example.android.datatracker.utilities.DataAdapter;
import com.example.android.datatracker.data.DataContract.DataEntry;

import at.grabner.circleprogress.CircleProgressView;


public class DataFragment extends Fragment implements DataAdapter.EntryOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    public static final int DATA_LOADER_ID = 100;
    public static final int EDIT_LOADER_ID = 200;
    private RecyclerView mList;
    private DataAdapter mAdapter;
    private Uri mCurrentUri;
    private String mNameTask;
    private double mDuration;
    private double mInterval;
    private int mTaskId;
    private Uri mEditUri;
    private ProgressBar mLoader;
    private CircleProgressView mCircleView;
    private boolean isDataChanged;
    private TextView mEmptyTitleTextView;
    private TextView mEmptySubTitleTextView;

    private EditText mET_XValue;
    private EditText mET_YValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_data, container, false);

        //Load a circle progress bar as loading bar
        mLoader = (ProgressBar) rootView.findViewById(R.id.loading_indicator);
        //Empty views
        mEmptyTitleTextView = (TextView) rootView.findViewById(R.id.empty_title_text);
        mEmptySubTitleTextView = (TextView) rootView.findViewById(R.id.empty_subtitle_text);
        mCircleView = (CircleProgressView) rootView.findViewById(R.id.cv_completing);
        mCircleView.setShowBlock(true);
        mList = (RecyclerView) rootView.findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);

        mAdapter = new DataAdapter(getContext(), this);
        mList.setAdapter(mAdapter);

        ItemTouchHelper swipeDelete = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (int) viewHolder.itemView.getTag();
                Uri uriTask = DataEntry.CONTENT_URI;
                uriTask = uriTask.buildUpon().appendPath(Integer.toString(id)).build();
                try {
                    int rowsDeleted = getActivity().getContentResolver().delete(uriTask, null, null);
                    if (rowsDeleted == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(getContext(), getString(R.string.delete_entries_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // Otherwise, the deletion was successful and displays a toast.
                        Toast.makeText(getContext(), getString(R.string.delete_entries_successful),
                                Toast.LENGTH_SHORT).show();
                        isDataChanged = true;
                    }
                    getActivity().getSupportLoaderManager().restartLoader(DATA_LOADER_ID, null, DataFragment.this);
                } catch (IllegalArgumentException e) {
                    System.out.println(e);
                }
            }
        });
        swipeDelete.attachToRecyclerView(mList);

        // Examine the intent used to launch this activity
        Intent intent = getActivity().getIntent();
        mCurrentUri = intent.getData();
        mNameTask = intent.getStringExtra("name");
        mTaskId = Integer.valueOf(mCurrentUri.getLastPathSegment());
        mDuration = intent.getDoubleExtra("duration", 0);
        mInterval = intent.getDoubleExtra("interval", 0);
        getActivity().setTitle(mNameTask);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = getString(R.string.editor_activity_title_new_data);
                String description = getString(R.string.editor_activity_desc_new_data);
                alertDialogMessage(title, description, true);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(DATA_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Activity activity = getActivity();
            if (activity != null)
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onClickDelete(int id, int position) {

    }

    @Override
    public void onClickView(int id, int position) {
        Uri uriTask = DataEntry.CONTENT_URI;
        mEditUri = uriTask.buildUpon().appendPath(Integer.toString(id)).build();
        getActivity().getSupportLoaderManager().restartLoader(EDIT_LOADER_ID, null, this);
    }

    @Override
    public void onClickEdit(int id, int position) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EDIT_LOADER_ID:
                return new CursorLoader(getContext(),
                        mEditUri,
                        null,//projection,
                        null,
                        null,
                        null);
            case DATA_LOADER_ID:
                String selection = DataEntry.COLUMN_TASK_ID + "=?";
                String selectionArgs1[] = {String.valueOf(mTaskId)};
                Uri dataUri = DataEntry.CONTENT_URI;
                String sortOrder = DataEntry.COLUMN_CREATION_DATE + " ASC";
                return new CursorLoader(getContext(),
                        dataUri,
                        null,//projection,
                        selection,
                        selectionArgs1,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == EDIT_LOADER_ID) {
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }
            // Proceed with moving to the first row of the cursor and reading data from it
            if (cursor.moveToFirst()) {
                double x = cursor.getDouble(cursor.getColumnIndex(DataEntry.COLUMN_X));
                double y = cursor.getDouble(cursor.getColumnIndex(DataEntry.COLUMN_Y));
                String title = getString(R.string.editor_activity_title_edit_data);
                String description = getString(R.string.editor_activity_desc_edit_data);
                alertDialogMessage(title, description, false);
                mET_XValue.setText(String.valueOf(x), TextView.BufferType.EDITABLE);
                mET_YValue.setText(String.valueOf(y), TextView.BufferType.EDITABLE);
            }
        }
        if (loader.getId() == DATA_LOADER_ID) {
            Log.e("DataFragment_LoaderId", "DATA_LOADER_ID");
            mLoader.setVisibility(View.GONE);
            mAdapter.swapCursor(cursor);
            double taskCompleted = mAdapter.getItemCount();
            double total = mDuration / mInterval;
            double progress = taskCompleted / total * 100;
            mCircleView.setValueAnimated((int) progress, 1500);
            if (isDataChanged) {
                ContentValues data = new ContentValues();
                data.put(TasksEntry.COLUMN_TASKS_COMPLETED, taskCompleted);
                try {
                    int rowsAffected = getActivity().getContentResolver().update(mCurrentUri, data, null, null);
                    if (rowsAffected == 0) {
                        Log.i("DataFragment", "Failed update taskCompleted in Tasks table");
                    } else {
                        // Otherwise, the update was successful and displays a toast.
                        Log.i("DataFragment", "Successful update taskCompleted in Tasks table");
                    }
                    isDataChanged = false;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        mLoader.setVisibility(View.GONE);
        if (cursor.getCount() == 0) {
            mEmptyTitleTextView.setVisibility(View.VISIBLE);
            mEmptySubTitleTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTitleTextView.setVisibility(View.INVISIBLE);
            mEmptySubTitleTextView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == EDIT_LOADER_ID) {
            mET_XValue.setText("");
            mET_YValue.setText("");
        }
        if (loader.getId() == DATA_LOADER_ID) {
            mAdapter.swapCursor(null);
        }

    }

    //build an alert dialog message for no internet connection
    public void alertDialogMessage(String title, String message, final boolean isNew) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View input = inflater.inflate(R.layout.edit_dialog, null);
        mET_XValue = (EditText) input.findViewById(R.id.et_X);
        mET_YValue = (EditText) input.findViewById(R.id.et_Y);
        mET_XValue.setHint(getString(R.string.dialog_add_new_data));
        mET_YValue.setHint(getString(R.string.dialog_add_new_data));
        if (!isNew) {

        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.EditDialog);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_edit_24dp);
        builder.setMessage(message);
        builder.setView(input);
        builder.setPositiveButton(getString(R.string.action_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isNew) {
                    save(mCurrentUri, true);
                } else {
                    save(mEditUri, false);
                }

            }
        });
        builder.setNegativeButton(getString(R.string.close_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void save(Uri uri, boolean isNew) {

        String x = mET_XValue.getText().toString().trim();
        String y = mET_YValue.getText().toString().trim();

        try {
            if (isNew) {
                ContentValues data = new ContentValues();
                data.put(DataEntry.COLUMN_NAME, mNameTask);
                data.put(DataEntry.COLUMN_CREATION_DATE, System.currentTimeMillis());
                data.put(DataEntry.COLUMN_TASK_ID, mTaskId);
                data.put(DataEntry.COLUMN_X, x);
                data.put(DataEntry.COLUMN_Y, y);
                Uri newUri = getActivity().getContentResolver().insert(DataEntry.CONTENT_URI, data);

                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(getContext(), getString(R.string.saving_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and displays a toast.
                    Toast.makeText(getContext(), getString(R.string.saving_successful),
                            Toast.LENGTH_SHORT).show();
                    isDataChanged = true;
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(DataEntry.COLUMN_X, x);
                values.put(DataEntry.COLUMN_Y, y);
                //values.put(LabelsEntry.COLUMN_X_LABEL, x_label);
                //values.put(LabelsEntry.COLUMN_Y_LABEL, y_label);
                // Otherwise this is an EXISTING item, this update the item with content URI
                int rowsAffected = getActivity().getContentResolver().update(uri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(getContext(), getString(R.string.saving_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and displays a toast.
                    Toast.makeText(getContext(), getString(R.string.saving_successful),
                            Toast.LENGTH_SHORT).show();
                    isDataChanged = true;
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
        getActivity().getSupportLoaderManager().destroyLoader(EDIT_LOADER_ID);
    }
}
