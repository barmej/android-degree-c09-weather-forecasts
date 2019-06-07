package com.barmej.weatherforecasts.data.sync;

import android.content.Context;
import android.content.Intent;

/**
 * Utility class used for data syncing operations
 */
public class SyncUtils {

    /**
     * Start syncing the data immediately by starting the intent service
     */
    public static void sync(Context context) {
        Intent intent = new Intent(context, SyncIntentService.class);
        context.startService(intent);
    }

}
