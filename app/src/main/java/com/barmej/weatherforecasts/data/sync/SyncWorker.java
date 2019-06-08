package com.barmej.weatherforecasts.data.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.barmej.weatherforecasts.data.WeatherDataRepository;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Sync the local data with the data from the API
        WeatherDataRepository repository = WeatherDataRepository.getInstance(getApplicationContext());
        repository.updateWeatherInfo();
        repository.updateForecastLists();
        return Result.success();
    }

}
