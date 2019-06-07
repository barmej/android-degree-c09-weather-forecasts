package com.barmej.weatherforecasts.data.sync;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.barmej.weatherforecasts.data.WeatherDataRepository;

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
    protected void onHandleIntent(@Nullable Intent intent) {
        // Sync the local data with the data from the API
        WeatherDataRepository repository = WeatherDataRepository.getInstance(this);
        repository.updateWeatherInfo();
        repository.updateForecastLists();
    }

}
