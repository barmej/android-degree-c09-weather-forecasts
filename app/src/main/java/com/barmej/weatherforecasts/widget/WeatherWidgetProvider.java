package com.barmej.weatherforecasts.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.WeatherDataRepository;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activities.MainActivity;
import com.barmej.weatherforecasts.utils.CustomDateUtils;
import com.barmej.weatherforecasts.utils.WeatherUtils;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidgetProvider extends AppWidgetProvider {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        // Get current widget width and height
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int widgetHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int widgetWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        // Construct the RemoteViews object
        final RemoteViews views;

        // Determine the widget layout file based on the current widget width and height
        if (widgetHeight >= 120 && widgetWidth >= 180) {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_large);
        } else if (widgetWidth > 120) {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_medium);
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.weather_widget_small);
        }


        // Get the data from the data base
        final WeatherDataRepository weatherDataRepository = WeatherDataRepository.getInstance(context);
        LiveData<WeatherInfo> weatherInfo = weatherDataRepository.getWeatherInfo();

        weatherInfo.observeForever(new Observer<WeatherInfo>() {
            @Override
            public void onChanged(@Nullable WeatherInfo weatherInfo) {

                // Make sure we got the data successfully
                if (weatherInfo != null) {

                    // Get human readable string from timestamp using getFriendlyDateString utility method
                    String dateString = CustomDateUtils.getFriendlyDateString(context, weatherInfo.getDt(), false);

                    // Display friendly date string
                    views.setTextViewText(R.id.appwidget_text_date, dateString);

                    // Get formatted temperature with degree symbol
                    String temperature = context.getString(R.string.format_temperature, weatherInfo.getMain().getTemp());

                    // Display current temperature
                    views.setTextViewText(R.id.appwidget_text_temperature, temperature);

                    // Use getWeatherIcon utility method to get weather icon resource ID
                    int weatherImageId = WeatherUtils.getWeatherIcon(weatherInfo.getWeather().get(0).getIcon());

                    // Display the icon
                    views.setImageViewResource(R.id.appwidget_image_weather_icon, weatherImageId);

                    // Get weather condition description
                    String weatherDescription = weatherInfo.getWeather().get(0).getDescription();

                    // Set weather icon content description for accessibility purpose
                    views.setContentDescription(R.id.appwidget_image_weather_icon, weatherDescription);

                    // Display the current city name
                    views.setTextViewText(R.id.appwidget_text_city, weatherInfo.getName());

                } else {

                    // If database is empty, try to get data from the internet and update the db
                    weatherDataRepository.getWeatherInfo();

                }

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });

        // Set click Listener to open main activity when the user click on the main widget view
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_container, pendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context, appWidgetManager, appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
