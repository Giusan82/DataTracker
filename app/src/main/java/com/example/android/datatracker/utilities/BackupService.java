package com.example.android.datatracker.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class BackupService extends JobService {
    private AsyncTask mBackgroundTask;
    private Context mContext = this;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.e("BackupService", "SERVICE STARTED");
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Tasks.execute(mContext, Tasks.SHOW_NOTIFICATION);
                //Tasks.sheduleNotification(getApplicationContext(), true);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(jobParameters, false); // if the job is successful, we don't need to reschedule (false)
            }
        }.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }
        return true; //it is true, because as soon as the conditions are re-met again, the job should be retried again
    }
}
