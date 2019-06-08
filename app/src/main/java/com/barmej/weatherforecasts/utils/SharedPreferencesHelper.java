package com.barmej.weatherforecasts.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.barmej.weatherforecasts.R;

/**
 * A helper class used to access SharedPreferences file and read user preferences
 */
public final class SharedPreferencesHelper {

    /*
     * Sunrise & Sunset hours in the day to be used to determine the background color
     */
    private static final String PREF_SUNRISE_HOUR = "PREF_SUNRISE_HOUR";
    private static final String PREF_SUNSET_HOUR = "PREF_SUNSET_HOUR";

    /**
     * The time of the last notification appearance
     */
    private static final String PREF_LAST_NOTIFICATION_APPEARANCE = "PREF_LAST_NOTIFICATION_APPEARANCE";


    /**
     * Helper method to handle setting sunrise hour in Preferences
     *
     * @param context Context used to get the SharedPreferences
     * @param hour    sunrise hour in the day
     */
    public static void setSunriseHour(Context context, int hour) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_SUNRISE_HOUR, hour).apply();
    }

    /**
     * Helper method to handle setting sunset hour in Preferences
     *
     * @param context Context used to get the SharedPreferences
     * @param hour    sunset the hour in the day
     */

    public static void setSunsetHour(Context context, int hour) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(PREF_SUNSET_HOUR, hour).apply();
    }

    /**
     * @param context Context used to get the SharedPreferences
     * @return sunrise hour in the day
     */
    public static int getSunriseHour(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_SUNRISE_HOUR, 0);
    }

    /**
     * @param context Context used to get the SharedPreferences
     * @return sunset hour in the day
     */
    public static int getSunsetHour(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_SUNSET_HOUR, 0);
    }

    /**
     * Saves the time that a notification hen the notification shown.
     * This will be used to get the elapsed time since a notification was shown.
     *
     * @param context            Used to access SharedPreferences
     * @param timeOfNotification Time of last notification to save in milliseconds
     */
    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(PREF_LAST_NOTIFICATION_APPEARANCE, timeOfNotification).apply();
    }

    /**
     * Returns the last time when a notification was shown in milliseconds
     *
     * @param context Used to access SharedPreferences
     * @return the time when the last notification was shown in milliseconds
     */
    public static long getLastNotificationTimeInMillis(Context context) {
        // As usual, we use the default SharedPreferences to access the user's preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // Get the last notification time in milliseconds when, 0 if no time saved before
        return sp.getLong(PREF_LAST_NOTIFICATION_APPEARANCE, 0);
    }


    /**
     * Returns the elapsed time in milliseconds since the last notification was shown. This is used
     * as part of our check to see if we should show another notification when the weather is
     * updated.
     *
     * @param context Used to access SharedPreferences as well as use other utility methods
     * @return Elapsed time in milliseconds since the last notification was shown
     */
    public static long getElapsedTimeSinceLastNotification(Context context) {
        long lastNotificationTimeMillis = SharedPreferencesHelper.getLastNotificationTimeInMillis(context);
        return System.currentTimeMillis() - lastNotificationTimeMillis;
    }

    /**
     * Returns the location currently set in Preferences. The default location this method
     * will return is "State of Kuwait", the home of the Barmej.com!
     *
     * @param context Context used to access SharedPreferences
     * @return Location the location set in SharedPreferences.
     */
    public static String getPreferredWeatherLocation(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String locationKey = context.getString(R.string.pref_location_key);
        String defaultLocation = context.getString(R.string.pref_location_default);
        return sp.getString(locationKey, defaultLocation);
    }

    /**
     * Returns the preferred measurement system set in Preferences. The default is metric
     *
     * @param context Context used to get the SharedPreferences
     * @return metric or imperial
     */
    public static String getPreferredMeasurementSystem(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String unitKey = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        return sp.getString(unitKey, defaultUnits);
    }


}