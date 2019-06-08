package com.barmej.weatherforecasts.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.format.DateUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.barmej.weatherforecasts.R;
import com.barmej.weatherforecasts.data.entity.WeatherInfo;
import com.barmej.weatherforecasts.ui.activities.MainActivity;

public class NotificationUtils {


    private static final String WEATHER_STATUS_CHANNEL_ID = "Weather Status";


    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1 is just random and not significant.
     */
    private static final int WEATHER_NOTIFICATION_ID = 1;

    /**
     * Create a new notification channel for weather status update notifications
     *
     * @param context Context to be used to get NotificationManager from system services
     */
    public static void createWeatherStatusNotificationChannel(Context context) {
        /*
         * Create the NotificationChannel, but only on API 26+ because the NotificationChannel class
         * is new and available only from Android 8 (API 26) and above.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Define channel name and description
            CharSequence name = context.getString(R.string.weather_notification_channel_name);
            String description = context.getString(R.string.weather_notification_channel_description);
            // Create NotificationChannel object and set channel description
            NotificationChannel channel = new NotificationChannel(WEATHER_STATUS_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after for this channel later
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Create and show a notification for the updated weather today.
     *
     * @param context     Context to be used in building and showing the notification
     * @param weatherInfo the weather data object to get the notification text and icons
     */
    public static void showWeatherStatusNotification(Context context, WeatherInfo weatherInfo) {

        if (weatherInfo == null) return;

        // Get the last time since we show the previous notification
        long timeSinceLastNotification = SharedPreferencesHelper
                .getElapsedTimeSinceLastNotification(context);

        // We only want to show the notification if we haven't shown a notification in the past day.
        if (timeSinceLastNotification < DateUtils.DAY_IN_MILLIS) return;

        // Notification Title
        String notificationTitle = context.getString(R.string.app_name);

        // Notification Text
        String notificationText = context.getString(
                R.string.format_notification,
                weatherInfo.getWeather().get(0).getDescription(),
                weatherInfo.getMain().getTempMax(),
                weatherInfo.getMain().getTempMin());


        // Small Icon
        int smallIcon = WeatherUtils.getWeatherIcon(weatherInfo.getWeather().get(0).getIcon());

        // Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), smallIcon);


        /*
         * NotificationCompat Builder is a very convenient way to build backward-compatible
         * notifications. In order to use it, we provide a context and specify a color for the
         * notification, a couple of different icons, the title for the notification, and
         * finally the text of the notification, which in our case in a summary of today's
         * forecast.
         */
        NotificationCompat.Builder notificationBuilder
                = new NotificationCompat.Builder(context, WEATHER_STATUS_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true);

        // This Intent will be triggered when the user clicks the notification
        Intent intent = new Intent(context, MainActivity.class);

        // Create a new TaskStackBuilder
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

        // Add the intent to the task stack builder with parent stack
        taskStackBuilder.addNextIntentWithParentStack(intent);

        // Create a pending intent to be used by the notification to fire the original intent
        PendingIntent pendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set notification intent to be called when the user click on the notification
        notificationBuilder.setContentIntent(pendingIntent);

        // Create notification object using builder's build method
        Notification notification = notificationBuilder.build();

        // Get instance of NotificationManager
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification, the id can be used to cancel or update the notification later
        assert notificationManager != null;
        notificationManager.notify(WEATHER_NOTIFICATION_ID, notification);

        // We just showed a notification, save the current time as last notification time
        SharedPreferencesHelper.saveLastNotificationTime(context, System.currentTimeMillis());

    }

}