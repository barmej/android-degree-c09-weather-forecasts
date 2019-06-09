package com.barmej.weatherforecasts.data.sync;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.utils.NotificationUtils;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate thread.
 */
public class SyncIntentService extends IntentService {

    /**
     * Creates an IntentService.
     * Invoked by the subclass's constructor.
     */
    public SyncIntentService() {
        super(SyncIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = NotificationUtils.getSyncingNotification(this);
        startForeground(NotificationUtils.SYNC_SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Sync the local data with the data from the API
        WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 5000);
        return START_STICKY;
    }

}
