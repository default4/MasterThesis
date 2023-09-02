package com.example.masterrunning.presentation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CheckTraining {

    public static void checkAndUpdateTraining(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        int onTraining = ((SharedPreferences) sharedPreferences).getInt("OnTraining", 0);

        // Get available_days from SharedPreferences
        String availableDays = sharedPreferences.getString("available_days", "");

        // Check if today is one of the available days
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDay = sdf.format(Calendar.getInstance().getTime());

        if (availableDays != null && availableDays.contains(currentDay)) {
            // Show push-up message
            showTrainingReminder(context);
        }

        if (onTraining == 0) {
            // No training today, update the workoutHistory
            Gson gson = new Gson();
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
