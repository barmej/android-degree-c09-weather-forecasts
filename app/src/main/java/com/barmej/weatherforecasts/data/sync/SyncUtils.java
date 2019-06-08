package com.barmej.weatherforecasts.data.sync;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Utility class used for data syncing operations
 */
public class SyncUtils {

    /**
     * Schedules a repeating data sync work using WorkManager.
     *
     * @param context Context used to get JobScheduler system service
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static void scheduleSync(Context context) {

        // Constrains declaration
        Constraints constraints = new Constraints.Builder()
                //  Network constraints on which this Job should run
                .setRequiredNetworkType(NetworkType.CONNECTED)
                // Call the builder's build method build the Constraints object
                .build();

        // Create PeriodicWorkRequest to periodically sync weather data
        PeriodicWorkRequest periodicWorkRequest =
                // Make this Worker repeated after specified interval
                new PeriodicWorkRequest.Builder(SyncWorker.class, 3, TimeUnit.HOURS)
                        // Set the constraints
                        .setConstraints(constraints)
                        // Call the builder's build method build the PeriodicWorkRequest
                        .build();

        // Get instance of WorkManager
        WorkManager workManager = WorkManager.getInstance();

        // Enqueues the periodic work request
        workManager.enqueueUniquePeriodicWork(
                "SyncWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest);

    }

    /**
     * Start syncing the data immediately by starting the intent service
     */
    public static void startSync(Context context) {
        Intent intent = new Intent(context, SyncIntentService.class);
        context.startService(intent);
    }

}
