package com.barmej.weatherforecasts.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.barmej.weatherforecasts.data.database.AppDatabase;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.network.NetworkUtils;

import java.io.IOException;

import retrofit2.Response;

/**
 * Worker class implementation for weather data sync
 */
public class WeatherDataSyncWorker extends Worker {

    private static final String TAG = WeatherDataSyncWorker.class.getSimpleName();

    public WeatherDataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        NetworkUtils networkUtils = NetworkUtils.getInstance(getApplicationContext());
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        try {
            Response<WeatherInfo> weatherInfoResponse = networkUtils.getApiInterface()
                    .getWeatherInfo(networkUtils.getQueryMap())
                    .execute();
            if (weatherInfoResponse.code() == 200) {
                WeatherInfo weatherInfo = weatherInfoResponse.body();
                if (weatherInfo != null) {
                    appDatabase.weatherInfoDao().deleteAllWeatherInfo();
                    appDatabase.weatherInfoDao().addWeatherInfo(weatherInfo);
                }
                appDatabase.weatherInfoDao().updateWeatherInfo(weatherInfoResponse.body());
                Log.i(TAG, "Weather Data Updated Successfully");
                return Result.success();
            } else
                Log.e(TAG, "Error while updating weather data");
            return Result.failure();
        } catch (IOException e) {
            Log.e(TAG, "Error while updating weather data");
            return Result.failure();
        }
    }
}
