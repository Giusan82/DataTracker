package com.example.android.datatracker.utilities;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;


public class TaskService extends IntentService {
    public TaskService() {
        super("TaskService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action.equals(Tasks.SHOW_ALARM)) {
            Log.i("TaskService", "The Alarm service is not been implemented! Action: " + action);
        }
    }
}
