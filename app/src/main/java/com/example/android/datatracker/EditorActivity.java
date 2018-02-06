package com.example.android.datatracker;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.DialogFragment;


import com.example.android.datatracker.data.TasksContract.TasksEntry;
import com.example.android.datatracker.utilities.TaskService;
import com.example.android.datatracker.utilities.Tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        CompoundButton.OnCheckedChangeListener {
    private static final int TASKS_LOADER = 0;
    private static final String TIME_PICKER_TAG = "TimePicker";
    private EditText mName;
    private EditText mDescription;
    private long mDuration;
    private EditText mDurationET;
    private Spinner mDurationSpinner;
    private long mFrequency;
    private EditText mFrequencyET;
    private Spinner mFrequencySpinner;
    private static long sStartTime;
    private static Button sBuTime;
    private SwitchCompat mAlarm;
    private int mAlarmActive;
    private Uri mCurrentUri;
    private long mDurationUnit = TasksEntry.MINUTE;
    private long mIntervalUnit = TasksEntry.MINUTE;
    private int mAlarmId;
    private static Calendar sTime;
    private SharedPreferences sharedPrefs;
    private String mTheme;
    private MenuItem mSaveButton;
    private boolean mHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        mTheme = sharedPrefs.getString(getString(R.string.settings_theme_key), getString(R.string.settings_theme_default));
        if (mTheme.equals(getString(R.string.settings_coral_theme_value))) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppThemeGreen);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mName = (EditText) findViewById(R.id.et_job_name);
        mDescription = (EditText) findViewById(R.id.et_job_description);
        mDurationET = (EditText) findViewById(R.id.et_duration);
        mDurationSpinner = (Spinner) findViewById(R.id.s_duration);
        mFrequencyET = (EditText) findViewById(R.id.et_frequency);
        mFrequencySpinner = (Spinner) findViewById(R.id.s_frequency);
        sBuTime = (Button) findViewById(R.id.bt_startTime);
        mAlarm = (SwitchCompat) findViewById(R.id.sw_alarm);
        mAlarm.setOnCheckedChangeListener(this);
        durationSpinner();
        frequencySpinner();

        // Examine the intent used to launch this activity
        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        // If the intent DOES NOT contain a URI load the editor for add new item
        if (mCurrentUri != null) {
            setTitle(getString(R.string.editor_activity_title_editor));
            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(TASKS_LOADER, null, this);
        } else {
            setTitle(getString(R.string.editor_activity_title_new_task));
            // Invalidate the options menu
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton switchButton, boolean isChecked) {
        if (isChecked) {
            mAlarmActive = 1;
        } else {
            mAlarmActive = 0;
        }
    }

    private void durationSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        durationAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // Apply the adapter to the spinner
        mDurationSpinner.setAdapter(durationAdapter);

        // Set the integer mSelected to the constant values
        mDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.time_seconds))) {
                        mDurationUnit = TasksEntry.SECOND;
                    } else if (selection.equals(getString(R.string.time_minutes))) {
                        mDurationUnit = TasksEntry.MINUTE;
                    } else if (selection.equals(getString(R.string.time_hours))) {
                        mDurationUnit = TasksEntry.HOUR;
                    } else if (selection.equals(getString(R.string.time_days))) {
                        mDurationUnit = TasksEntry.DAY;
                    } else {
                        mDurationUnit = TasksEntry.WEEK;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDurationUnit = TasksEntry.SECOND;
            }
        });
    }

    private void frequencySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter frequencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        // Apply the adapter to the spinner
        mFrequencySpinner.setAdapter(frequencyAdapter);

        // Set the integer mSelected to the constant values
        mFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.time_seconds))) {
                        mIntervalUnit = TasksEntry.SECOND;
                    } else if (selection.equals(getString(R.string.time_minutes))) {
                        mIntervalUnit = TasksEntry.MINUTE;
                    } else if (selection.equals(getString(R.string.time_hours))) {
                        mIntervalUnit = TasksEntry.HOUR;
                    } else if (selection.equals(getString(R.string.time_days))) {
                        mIntervalUnit = TasksEntry.DAY;
                    } else {
                        mIntervalUnit = TasksEntry.WEEK;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mIntervalUnit = TasksEntry.SECOND;
            }
        });
    }

    public void startingTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), TIME_PICKER_TAG);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Use the current time as the default values for the time picker
            sTime = Calendar.getInstance();
            int hour = sTime.get(Calendar.HOUR_OF_DAY);
            int minute = sTime.get(Calendar.MINUTE);

            //Create and return a new instance of TimePickerDialog
            TimePickerDialog timePicker = new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
            return timePicker;
        }

        //onTimeSet() callback method
        public void onTimeSet(TimePicker view, int hours, int min) {
            sTime.set(Calendar.HOUR_OF_DAY, hours);
            sTime.set(Calendar.MINUTE, min);
            sTime.set(Calendar.SECOND, 0);
            sStartTime = sTime.getTimeInMillis();
            Date date = new Date(sStartTime);
            String time = formatTime(date);
            sBuTime.setText(getString(R.string.starting_time_active, time));
        }
    }

    private static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ROOT);
        return timeFormat.format(dateObject);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Define a projection that specifies which columns from the database is used after the query.
        String[] projection = {
                TasksEntry._ID,
                TasksEntry.COLUMN_NAME,
                TasksEntry.COLUMN_DESCRIPTION,
                TasksEntry.COLUMN_STARTING_TIME,
                TasksEntry.COLUMN_DURATION,
                TasksEntry.COLUMN_INTERVAL,
                TasksEntry.COLUMN_DURATION_UNIT,
                TasksEntry.COLUMN_INTERVAL_UNIT,
                TasksEntry.COLUMN_ALARM,
                TasksEntry.COLUMN_ALARM_ID
        };
        switch (id){
            case TASKS_LOADER:
                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(
                        this,
                        mCurrentUri,
                        projection,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        if (data.moveToFirst()) {
            String name = data.getString(data.getColumnIndex(TasksEntry.COLUMN_NAME));
            mName.setText(name);
            String description = data.getString(data.getColumnIndex(TasksEntry.COLUMN_DESCRIPTION));
            mDescription.setText(description);
            mDuration = data.getLong(data.getColumnIndex(TasksEntry.COLUMN_DURATION));
            mDurationET.setText(String.valueOf(mDuration));
            mFrequency = data.getLong(data.getColumnIndex(TasksEntry.COLUMN_INTERVAL));
            mFrequencyET.setText(String.valueOf(mFrequency));
            mDurationUnit = data.getLong(data.getColumnIndex(TasksEntry.COLUMN_DURATION_UNIT));
            mDurationSpinner.setSelection(retrieveUnit(mDurationUnit));
            mIntervalUnit = data.getLong(data.getColumnIndex(TasksEntry.COLUMN_INTERVAL_UNIT));
            mFrequencySpinner.setSelection(retrieveUnit(mIntervalUnit));
            Date date = new Date(data.getLong(data.getColumnIndex(TasksEntry.COLUMN_STARTING_TIME)));
            String time = formatTime(date);
            sBuTime.setText(getString(R.string.starting_time_active, time));
            mAlarmActive = data.getInt(data.getColumnIndex(TasksEntry.COLUMN_ALARM));
            if (mAlarmActive == 1) {
                mAlarm.setChecked(true);
            } else {
                mAlarm.setChecked(false);
            }
            mAlarmId = data.getInt(data.getColumnIndex(TasksEntry.COLUMN_ALARM_ID));
        }
    }

    private int retrieveUnit(long value) {
        int unit;
        if (value == TasksEntry.MINUTE) {
            unit = 1;
        } else if (value == TasksEntry.HOUR) {
            unit = 2;
        } else if (value == TasksEntry.DAY) {
            unit = 3;
        } else if (value == TasksEntry.WEEK) {
            unit = 4;
        } else {
            unit = 0;
        }
        return unit;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mDurationET.setText("");
        mFrequencyET.setText("");
        mDurationSpinner.setSelection(0);
        mFrequencySpinner.setSelection(0);
    }

    private void save() {
        String name = mName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_name_field), Toast.LENGTH_SHORT).show();
            return;
        }
        String description = mDescription.getText().toString().trim();
        long creation_time = System.currentTimeMillis();
        if (mDurationET.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_duration_field), Toast.LENGTH_SHORT).show();
            return;
        }
        mDuration = Long.valueOf(mDurationET.getText().toString().trim());

        if (mFrequencyET.length() == 0) {
            Toast.makeText(this, getString(R.string.empty_frequency_field), Toast.LENGTH_SHORT).show();
            return;
        }
        mFrequency = Long.valueOf(mFrequencyET.getText().toString().trim());

        if (mCurrentUri == null) {
            mAlarmId = (int) creation_time / 1000;
        }
        ContentValues values = new ContentValues();
        values.put(TasksEntry.COLUMN_NAME, name);
        values.put(TasksEntry.COLUMN_CREATION_DATE, creation_time);
        values.put(TasksEntry.COLUMN_DURATION, mDuration);
        values.put(TasksEntry.COLUMN_INTERVAL, mFrequency);
        values.put(TasksEntry.COLUMN_DURATION_UNIT, mDurationUnit);
        values.put(TasksEntry.COLUMN_INTERVAL_UNIT, mIntervalUnit);
        values.put(TasksEntry.COLUMN_STARTING_TIME, sStartTime);
        values.put(TasksEntry.COLUMN_ALARM, mAlarmActive);
        values.put(TasksEntry.COLUMN_ALARM_ID, mAlarmId);
        values.put(TasksEntry.COLUMN_DESCRIPTION, description);
        try {
            if (mCurrentUri == null) {
                Uri newUri = getContentResolver().insert(TasksEntry.CONTENT_URI, values);
                // Show a toast message depending on whether or not the insertion was successful
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.saving_failed), Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and displays a toast.
                    Toast.makeText(this, getString(R.string.saving_successful_with_name, name),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise this is an EXISTING item, this update the item with content URI
                int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.saving_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and displays a toast.
                    Toast.makeText(this, getString(R.string.saving_successful_with_name, name),
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (mAlarmActive == 1) {
                alarmSetup(sStartTime, mFrequency, mAlarmId, true);
            } else {
                alarmSetup(sStartTime, mFrequency, mAlarmId, false);
            }
            //Exit activity
            finish();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }

    private void alarmSetup(long time, long interval, int id, boolean isActive) {
        interval = interval * mIntervalUnit;
        PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(this, TaskService.class);
        alarmIntent.setAction(Tasks.SHOW_ALARM);
        pendingIntent = PendingIntent.getService(this, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (isActive) {
            manager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
        } else {
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
            stopService(alarmIntent);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSaveButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor, menu);
        mSaveButton = menu.findItem(R.id.action_save);
        mName.addTextChangedListener(mTextWatcher);
        mDurationET.addTextChangedListener(mTextWatcher);
        mFrequencyET.addTextChangedListener(mTextWatcher);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                // Save to database
                save();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the item hasn't changed, continue with navigating up to parent activity
                if (!mHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //this listens for any text changing on a View
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mHasChanged = true;
            //this setup the save button visible changing text, indicating at the user that some changing has occurred
            mSaveButton.setVisible(true);
        }
    };

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.EditDialog);
        //this shows an alert message to inform the user that the changing will be lost
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        //this discard the changing
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, navigate to parent activity.
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
            }
        });
        // this dismisses the dialog clicking on "Keep editing"
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
