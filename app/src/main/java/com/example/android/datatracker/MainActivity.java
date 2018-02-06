package com.example.android.datatracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.datatracker.data.TasksContract.TasksEntry;
import com.example.android.datatracker.widgets.ItemTouchHelperCallback;
import com.example.android.datatracker.utilities.TaskAdapter;
import com.example.android.datatracker.utilities.Tasks;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        TaskAdapter.EntryOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int TASK_LOADER_ID = 0;

    private SQLiteDatabase mDb;
    private RecyclerView mList;
    private TaskAdapter mAdapter;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    private SharedPreferences sharedPrefs;
    private String mTheme;
    private Boolean mBackup;

    private ProgressBar mLoader;
    private ImageView mEmptyImaveView;
    private TextView mEmptyTitleTextView;
    private TextView mEmptySubTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mTheme = sharedPrefs.getString(getString(R.string.settings_theme_key), getString(R.string.settings_theme_default));
        if (mTheme.equals(getString(R.string.settings_coral_theme_value))) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeGreen);
        }

        Log.e("MainActivity->Theme", mTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load a circle progress bar as loading bar
        mLoader = (ProgressBar) findViewById(R.id.loading_indicator);
        //Empty views
        mEmptyImaveView = (ImageView) findViewById(R.id.empty_image);
        mEmptyTitleTextView = (TextView) findViewById(R.id.empty_title_text);
        mEmptySubTitleTextView = (TextView) findViewById(R.id.empty_subtitle_text);

        mList = (RecyclerView) findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mList.setLayoutManager(layoutManager);
        mList.setHasFixedSize(true);

        mAdapter = new TaskAdapter(this, this);
        mList.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        //Register MainActivity as an OnSharedPreferenceChangedListener to receive a callback when a SharedPreference has changed.
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        //This implements the ItemTouchHelper-extension that allow
        mCallback = new ItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mList);
    }

    @Override
    public void onClickDelete(int id, String name) {
//        int id = (int) viewHolder.iv_delete
        Uri uriTask = TasksEntry.CONTENT_URI;
        uriTask = uriTask.buildUpon().appendPath(Integer.toString(id)).build();
        try {
            int rowsDeleted = getContentResolver().delete(uriTask, null, null);
            if (rowsDeleted == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.delete_entries_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the deletion was successful and displays a toast.
                Toast.makeText(this, getString(R.string.delete_entries_successful, name),
                        Toast.LENGTH_SHORT).show();
            }
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);

            mItemTouchHelper.closeOpened();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    @Override
    public void onClickView(int id, String name, double duration, double interval) {
        Uri uriTask = TasksEntry.CONTENT_URI;
        uriTask = uriTask.buildUpon().appendPath(Integer.toString(id)).build();
        Intent intent = new Intent(MainActivity.this, JobActivity.class);
        intent.setData(uriTask);
        intent.putExtra("name", name);
        intent.putExtra("duration", duration);
        intent.putExtra("interval", interval);
        startActivity(intent);
    }

    @Override
    public void onClickEdit(int id, int position) {
        Uri uriTask = TasksEntry.CONTENT_URI;
        uriTask = uriTask.buildUpon().appendPath(Integer.toString(id)).build();
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.setData(uriTask);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TASK_LOADER_ID:
                Uri taskUri = TasksEntry.CONTENT_URI;
                String sortOrder = TasksEntry.COLUMN_CREATION_DATE + " DESC";
                String selection = null;
                return new CursorLoader(this,
                        taskUri,
                        null,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mList.smoothScrollToPosition(0);
        mLoader.setVisibility(View.GONE);
        if (cursor.getCount() == 0) {
            mEmptyImaveView.setVisibility(View.VISIBLE);
            mEmptyTitleTextView.setVisibility(View.VISIBLE);
            mEmptySubTitleTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyImaveView.setVisibility(View.INVISIBLE);
            mEmptyTitleTextView.setVisibility(View.INVISIBLE);
            mEmptySubTitleTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.backup_key))) {
            mBackup = sharedPrefs.getBoolean(getString(R.string.backup_key), getResources().getBoolean(R.bool.pref_enable_backup));
            Tasks.sheduleNotification(getApplicationContext(), mBackup);
        }
        if (key.equals(getString(R.string.settings_theme_key))) {
            this.recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
