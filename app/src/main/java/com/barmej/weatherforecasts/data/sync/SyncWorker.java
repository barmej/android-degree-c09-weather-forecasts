package com.barmej.weatherforecasts.data.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.NotificationUtils;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Sync the local data with the data from the API
        final WeatherDataRepository repository = WeatherDataRepository.getInstance(getApplicationContext());
        repository.updateWeatherInfo();
        repository.updateForecastLists();
        // Show notification after successful data change
        AppExecutor.getInstance().getMainThread().execute(new Runnable() {
            @Override
            public void run() {
                repository.getWeatherInfo().observeForever(new Observer<WeatherInfo>() {
                    @Override
                    public void onChanged(WeatherInfo weatherInfo) {
                        NotificationUtils.showWeatherStatusNotification(getApplicationContext(), weatherInfo);
                    }
                });
            }
        });
        return Result.success();
    }

}
