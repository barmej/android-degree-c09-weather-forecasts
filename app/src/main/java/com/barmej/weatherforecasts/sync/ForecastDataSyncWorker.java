package com.barmej.weatherforecasts.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.barmej.weatherforecasts.data.database.AppDatabase;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherForecasts;
import com.barmej.weatherforecasts.data.network.NetworkUtils;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;

import java.io.IOException;

import retrofit2.Response;

/**
 * Worker class implementation for forecast data sync
 */
public class ForecastDataSyncWorker extends Worker {

    private static final String TAG = ForecastDataSyncWorker.class.getSimpleName();

    public ForecastDataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        NetworkUtils networkUtils = NetworkUtils.getInstance(getApplicationContext());
        AppDatabase appDatabase = AppDatabase.getInstance(getApplicationContext());
        try {
            Response<WeatherForecasts> forecastInfoResponse = networkUtils.getApiInterface()
                    .getForecasts(networkUtils.getQueryMap())
                    .execute();
            if (forecastInfoResponse.code() == 200) {
                WeatherForecasts weatherForecasts = forecastInfoResponse.body();
                if (weatherForecasts != null) {
                    ForecastLists forecastLists = OpenWeatherDataParser.getForecastsDataFromWeatherForecasts(weatherForecasts);
                    appDatabase.forecastDao().deleteAllForecastsInfo();
                    appDatabase.forecastDao().addForecastsList(forecastLists);
                }
                Log.i(TAG, "Forecast Data Updated Successfully");
                return Result.success();
            } else
                Log.e(TAG, "Error while updating forecast data");
            return Result.failure();
        } catch (IOException e) {
            Log.e(TAG, "Error while updating forecast data");
            return Result.failure();
        }
    }
}
