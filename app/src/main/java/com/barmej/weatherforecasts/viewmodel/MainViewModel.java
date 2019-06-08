package com.barmej.weatherforecasts.viewmodel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.ForecastLists;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.data.sync.SyncUtils;

/**
 * ViewModel class that hold data requests and temporary that survive configuration changes
 */
public class MainViewModel extends AndroidViewModel {

    /**
     * An instance of WeatherDataRepository for all data related operations
     */
    private WeatherDataRepository mRepository;

    /**
     * LiveData object to wrap WeatherInfo  data
     */
    private LiveData<WeatherInfo> mWeatherInfoLiveData;

    /**
     * Forecasts object to wrap Forecasts data
     */
    private LiveData<ForecastLists> mForecastListsLiveData;

    /**
     * Context to be used when register and unregister BroadcastReceiver
     */
    private Context mContext;

    /**
     * BroadcastReceiver to receive network connectivity change events
     */
    BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SyncUtils.startSync(mContext);
        }
    };

    /**
     * ViewModel Constructor
     *
     * @param application An instance of application class
     */
    public MainViewModel(@NonNull Application application) {
        super(application);

        // Get the context from Application object
        mContext = application.getApplicationContext();

        // Get instance of WeatherDataRepository
        mRepository = WeatherDataRepository.getInstance(getApplication());

        // Request weather info data from the repository class
        mWeatherInfoLiveData = mRepository.getWeatherInfo();

        // Request forecasts lists  from the repository class
        mForecastListsLiveData = mRepository.getForecastsInfo();

        // If the database is empty, start syncing the data immediately !
        mWeatherInfoLiveData.observeForever(new Observer<WeatherInfo>() {
            @Override
            public void onChanged(WeatherInfo weatherInfo) {
                if (weatherInfo == null) {
                    SyncUtils.startSync(mContext);
                }
            }
        });

        // Register connectivity broadcast receiver
        IntentFilter connectivityIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mConnectivityReceiver, connectivityIntentFilter);

    }

    /**
     * Get a handle of WeatherInfo LiveData object
     *
     * @return A wrapper LiveData object contains the weather info
     */
    public LiveData<WeatherInfo> getWeatherInfoLiveData() {
        return mWeatherInfoLiveData;
    }

    /**
     * Get a handle of ForecastsList LiveData object
     *
     * @return A wrapper LiveData object contains forecasts lists
     */
    public LiveData<ForecastLists> getForecastListsLiveData() {
        return mForecastListsLiveData;
    }

    @Override
    protected void onCleared() {
        // Unregister connectivity broadcast receiver
        mContext.unregisterReceiver(mConnectivityReceiver);
        // Cancel all ongoing requests
        mRepository.cancelDataRequests();
    }

}
