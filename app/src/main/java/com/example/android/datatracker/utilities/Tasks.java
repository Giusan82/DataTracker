package com.example.android.datatracker.utilities;


import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class Tasks {
    public static final String SHOW_NOTIFICATION = "show_notification";
    public static final String SHOW_ALARM = "show_alarm";
    public static final int STARTING_NOTIFICATION = 5; //seconds
    private static final int SYNC_FLEXTIME = 10; //seconds
    private static final String JOB_TAG_NOTIFICATION = "task_notification";


    public static void execute(Context context, String action){
        if(SHOW_NOTIFICATION.equals(action)){
            NotificationsTask.execute(context);
        }
    }
    synchronized public static void sheduleNotification(@NonNull final Context context, boolean isActive){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job newJob = dispatcher.newJobBuilder()
                .setService(BackupService.class)
                .setTag(JOB_TAG_NOTIFICATION)
                .setConstraints(Constraint.ON_UNMETERED_NETWORK)  //only run when the device use WIFI
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true) //repeat the job
                .setTrigger(Trigger.executionWindow(STARTING_NOTIFICATION, STARTING_NOTIFICATION + SYNC_FLEXTIME))
                .setReplaceCurrent(true) //overwrite an existing job with the same tag
                .build();
        if(isActive){
            dispatcher.schedule(newJob);
        }else{
            dispatcher.cancel(JOB_TAG_NOTIFICATION);
        }
    }
}
