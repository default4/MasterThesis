package com.example.masterrunning.presentation;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CheckTraining {


    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TrainingAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void checkAndUpdateTraining(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int onTraining = sharedPreferences.getInt("OnTraining", 0);

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();

        // Get available_days from SharedPreferences
        String availableDays = sharedPreferences.getString("available_days", "");

        // Check if today is one of the available days
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDay = sdf.format(Calendar.getInstance().getTime());

        if ("Sunday".equals(currentDay)) {
            // Fetch dictionaries from SharedPreferences
            String burnedCaloriesJson = sharedPreferences.getString("burned_calories_dict", "");
            String stepCountJson = sharedPreferences.getString("step_count_dict", "");
            HashMap<String, Integer> burnedCaloriesDict = gson.fromJson(burnedCaloriesJson, type);
            HashMap<String, Integer> stepCountDict = gson.fromJson(stepCountJson, type);

            if (burnedCaloriesDict == null) burnedCaloriesDict = new HashMap<>();
            if (stepCountDict == null) stepCountDict = new HashMap<>();

            // Sum up the values in the dictionaries
            int totalCalories = 0;
            for (int calorie : burnedCaloriesDict.values()) {
                totalCalories += calorie;
            }

            int totalSteps = 0;
            for (int step : stepCountDict.values()) {
                totalSteps += step;
            }

            // Fetch history arrays from SharedPreferences
            String calorieHistoryJson = sharedPreferences.getString("calorie_history", "");
            String stepHistoryJson = sharedPreferences.getString("steps_history", "");
            ArrayList<Integer> calorieHistory = gson.fromJson(calorieHistoryJson, new TypeToken<ArrayList<Integer>>(){}.getType());
            ArrayList<Integer> stepHistory = gson.fromJson(stepHistoryJson, new TypeToken<ArrayList<Integer>>(){}.getType());

            if (calorieHistory == null) calorieHistory = new ArrayList<>();
            if (stepHistory == null) stepHistory = new ArrayList<>();

            // Shift and update history arrays
            if (!calorieHistory.isEmpty()) {
                calorieHistory.remove(0);
            }
            calorieHistory.add(totalCalories);

            if (!stepHistory.isEmpty()) {
                stepHistory.remove(0);
            }
            stepHistory.add(totalSteps);

            // Save updated history arrays back to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("calorie_history", gson.toJson(calorieHistory));
            editor.putString("steps_history", gson.toJson(stepHistory));
            editor.apply();
        }


        if (availableDays != null && availableDays.contains(currentDay)) {
            // Show push-up message
            showTrainingReminder(context);
        }

        if (onTraining == 0) {
            // No training today, update the workoutHistory
            String json = sharedPreferences.getString("workout_history", "");
            ArrayList<String> workoutHistory = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());

            // If for some reason workoutHistory is null, then initialize it
            if (workoutHistory == null) workoutHistory = new ArrayList<>();

            // Shift the values to the left
            for (int i = 0; i < workoutHistory.size() - 1; i++) {
                workoutHistory.set(i, workoutHistory.get(i + 1));
            }

            // Add "Rest" to the last position
            if(workoutHistory.isEmpty()) {
                workoutHistory.add("Rest");
            } else {
                workoutHistory.set(workoutHistory.size() - 1, "Rest");
            }

            // Save the updated workoutHistory list back to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            json = gson.toJson(workoutHistory);
            editor.putString("workout_history", json);
            editor.apply();
        } else {
            // Reset OnTraining to 0
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("OnTraining", 0);
            editor.apply();
        }
    }

    private static void showTrainingReminder(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "your_channel_id")
                .setSmallIcon(R.drawable.running) // set your own small icon
                .setContentTitle("Master Running")
                .setContentText("Don't forget about your training!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // TODO: Create NotificationChannel for Android O and above
        }

        // Show the notification
        notificationManager.notify(1, builder.build());
    }
}
