package com.barmej.weatherforecasts.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.barmej.weatherforecasts.data.database.AppDatabase;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherForecasts;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.network.NetworkUtils;
import com.barmej.weatherforecasts.data.sync.SyncUtils;
import com.barmej.weatherforecasts.utils.AppExecutor;
import com.barmej.weatherforecasts.utils.OpenWeatherDataParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class used for all data operations
 * It uses {@link NetworkUtils} class to access data from APIs
 * and {@link AppDatabase} class to access data from local database
 */
public class WeatherDataRepository {

    private static final String TAG = WeatherDataRepository.class.getSimpleName();

    /**
     * Object used for the purpose of synchronize lock
     */
    private static final Object LOCK = new Object();

    /**
     * Instance of this class for Singleton
     */
    private static WeatherDataRepository sInstance;

    /**
     * Instance of NetworkUtils to perform network operations
     */
    private NetworkUtils mNetworkUtils;

    /**
     * Instance of AppDatabase to perform database operation
     */
    private AppDatabase mAppDatabase;

    /**
     * Instance of AppExecutor to perform tasks on worker threads
     */
    private AppExecutor mAppExecutor;

    /**
     * Retrofit Call objects
     */
    private Call<WeatherInfo> mWeatherCall;
    private Call<WeatherForecasts> mForecastsCall;


    /**
     * Method used to get an instance of WeatherDataRepository class
     *
     * @param context Context to use for some initializations
     * @return an instance of WeatherDataRepository class
     */
    public static WeatherDataRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null)
                    sInstance = new WeatherDataRepository(context.getApplicationContext());
            }
        }
        return sInstance;
    }

    /**
     * @param context Context to use for some initializations
     */
    @TargetApi(Build.VERSION_CODES.N)
    private WeatherDataRepository(Context context) {
        mNetworkUtils = NetworkUtils.getInstance(context);
        mAppDatabase = AppDatabase.getInstance(context);
        mAppExecutor = AppExecutor.getInstance();
        // Schedule the job to sync weather data every period of time
        SyncUtils.scheduleSync(context);
    }

    /**
     * Get current weather data
     *
     * @return LiveData object to be notified when data change
     */
    public LiveData<WeatherInfo> getWeatherInfo() {
        // Get LiveData object from database using Room
        return mAppDatabase.weatherInfoDao().getWeatherInfo();
    }

    /**
     * Get forecasts data
     *
     * @return LiveData object to be notified when data change
     */
    public LiveData<ForecastLists> getForecastsInfo() {
        // Get LiveData object from database using Room
        return mAppDatabase.forecastDao().getForecasts();
    }

    /**
     * Empty weather info table and save new weather info to database
     */
    public void updateWeatherInfo() {

        // Create a new WeatherInfo call using Retrofit API interface
        mWeatherCall = mNetworkUtils.getApiInterface().getWeatherInfo(mNetworkUtils.getQueryMap());

        // Add request to the queue to be executed asynchronously
        mWeatherCall.enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(@NonNull Call<WeatherInfo> call, @NonNull Response<WeatherInfo> response) {
                if (response.code() == 200) {
                    // Get WeatherInfo object from response body
                    final WeatherInfo weatherInfo = response.body();
                    if (weatherInfo != null) {
                        AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                mAppDatabase.weatherInfoDao().deleteAllWeatherInfo();
                                mAppDatabase.weatherInfoDao().addWeatherInfo(weatherInfo);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherInfo> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    /**
     * Empty forecasts table and save new forecasts info to database
     */
    public void updateForecastLists() {

        // Create a new WeatherForecasts call using Retrofit API interface
        mForecastsCall = mNetworkUtils.getApiInterface().getForecasts(mNetworkUtils.getQueryMap());

        // Add request to the queue to be executed asynchronously
        mForecastsCall.enqueue(new Callback<WeatherForecasts>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecasts> call, @NonNull Response<WeatherForecasts> response) {
                if (response.code() == 200) {
                    final WeatherForecasts weatherForecasts = response.body();
                    if (weatherForecasts != null) {
                        AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                ForecastLists forecastLists = OpenWeatherDataParser.getForecastsDataFromWeatherForecasts(weatherForecasts);
                                mAppDatabase.forecastDao().deleteAllForecastsInfo();
                                mAppDatabase.forecastDao().addForecastsList(forecastLists);
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherForecasts> call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });

    }


    /**
     * Cancel all data requests
     */
    public void cancelDataRequests() {
        if (mWeatherCall != null) {
            mWeatherCall.cancel();
        }
        if (mForecastsCall != null) {
            mForecastsCall.cancel();
        }
    }


}