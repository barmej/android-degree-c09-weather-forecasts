package com.barmej.weatherforecasts.data.sync;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.concurrent.TimeUnit;

/**
 * Utility class used for data syncing operations
 */
public class SyncUtils {


    /**
     * Unique ID for the job
     */
    private static final int SYNC_SERVICE_JOB_ID = 0;

    /*
     * Interval at which to sync with the weather.
     */
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(3);

    /**
     * Schedules a repeating sync of weather data using JobScheduler.
     *
     * @param context Context used to get JobScheduler system service
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static void scheduleSync(Context context) {

        // Get an instance of JobScheduler service
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        // Create component name for the JobService we want to schedule
        ComponentName jobServiceName = new ComponentName(context, SyncJobService.class);

        // Create the Job to periodically sync weather data
        JobInfo jobInfo = new JobInfo.Builder(SYNC_SERVICE_JOB_ID, jobServiceName)
                //  Network constraints on which this Job should run
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                // The options are to keep the Job "forever" and not die after device reboot
                .setPersisted(true)
                // Make this job repeated after specified interval
                .setPeriodic(SYNC_INTERVAL_SECONDS)
                // Once the Job is ready, call the builder's build method to return the Job
                .build();

        // Schedule the Job with the JobScheduler
        jobScheduler.schedule(jobInfo);

    }

    /**
     * Start syncing the data immediately by starting the intent service
     */
    public static void startSync(Context context) {
        Intent intent = new Intent(context, SyncIntentService.class);
        context.startService(intent);
    }

}
