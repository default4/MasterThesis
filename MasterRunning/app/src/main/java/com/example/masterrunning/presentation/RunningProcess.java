package com.example.masterrunning.presentation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RunningProcess extends AppCompatActivity implements SensorEventListener {
    private TextView durationTextView;
    private TextView heartRateTextView;
    private TextView burnedCaloriesTextView;


    private CountDownTimer timer;
    private SensorManager sensorManager;
    private float initialStepCount = -1;
    int currentSteps = 0;
    private static final float MET_VALUE_WALKING = 3.8f;
    private float weightInKg;
    private final int PHYISCAL_ACTIVITY = 1;
    ImageButton pausePlayButton;
    int caloriesBurned = 0;
    int currentHeartRate = 0;
    boolean isPaused = false;
    private long timeLeftInMillis; // time left in milliseconds
    private long endTime; // when the timer will end (in system milliseconds)
    private Handler handler = new Handler(Looper.getMainLooper()); // Handler for the timer
    private Runnable runnable; // Runnable for the timer logic
    private long pauseTime;
    private TimerTask timerTask;
    private long elapsedTime = 0;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running_process);

        // Fetch weight from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        weightInKg = (float) Double.parseDouble(sharedPreferences.getString("weight", "0"));

        durationTextView = findViewById(R.id.durationTextView);
        heartRateTextView = findViewById(R.id.intensityTextView);
        burnedCaloriesTextView = findViewById(R.id.caloriesTextView);

        Intent intent = getIntent();
        String duration = intent.getStringExtra("duration");
        String intensity = intent.getStringExtra("intensity");
        String workout = intent.getStringExtra("type");

        long durationInMillis = Long.parseLong(duration) * 60 * 1000;
        endTime = System.currentTimeMillis() + durationInMillis;

        pausePlayButton = findViewById(R.id.pausePlayButton);
        pausePlayButton.setImageResource(android.R.drawable.ic_media_pause);

        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    resumeTimer();
                    pausePlayButton.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    pauseTimer();
                    pausePlayButton.setImageResource(android.R.drawable.ic_media_play);
                }
                isPaused = !isPaused;
            }
        });

        startTimer(durationInMillis);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }


        Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heartRateSensor == null) {
            heartRateTextView.setText("Heart Rate: Sensor not available");
        } else {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    private void startTimer(long durationInMillis) {
        timer = new CountDownTimer(durationInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!isPaused) {
                    timeLeftInMillis = millisUntilFinished;
                    updateUI();
                } else {
                    cancel();
                }
            }

            public void onFinish() {
                durationTextView.setText("Done!");

                // Vibrate the watch
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    // Vibrate for 500 milliseconds
                    vibrator.vibrate(500);
                }

                // Update workout history
                String currentWorkout = getIntent().getStringExtra("type");
                if (currentWorkout != null) {
                    updateWorkoutHistory(currentWorkout, currentSteps, caloriesBurned, currentHeartRate);
                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Timer", "Moving to StartRunningFragment");
                        moveToStartRunningFragment();
                    }
                }, 3000); // 3 seconds delay
            }
        }.start();
    }

    private void updateUI() {
        long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis);
        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) -
                TimeUnit.MINUTES.toSeconds(minutesLeft);
        durationTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutesLeft, secondsLeft));
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void resumeTimer() {
        startTimer(timeLeftInMillis);
    }





    private void updateWorkoutHistory(String currentWorkoutType, int steps, float calories, float heartRate) {
        // Retrieve the workoutHistory list from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("workout_history", "");
        ArrayList<String> workoutHistory = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());

        // If for some reason workoutHistory is null, then initialize it
        if (workoutHistory == null) workoutHistory = new ArrayList<>();

        // Shift the values to the left
        for (int i = 0; i < workoutHistory.size() - 1; i++) {
            workoutHistory.set(i, workoutHistory.get(i + 1));
        }

        // Add the current workout type to the last position
        if(workoutHistory.isEmpty()) {
            workoutHistory.add(currentWorkoutType);
        } else {
            workoutHistory.set(workoutHistory.size() - 1, currentWorkoutType);
        }

        // Get the current day of the week
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Convert the int dayOfWeek to its String representation
        String currentDay = null;
        switch(dayOfWeek) {
            case Calendar.MONDAY:
                currentDay = "Monday";
                break;
            case Calendar.TUESDAY:
                currentDay = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                currentDay = "Wednesday";
                break;
            case Calendar.THURSDAY:
                currentDay = "Thursday";
                break;
            case Calendar.FRIDAY:
                currentDay = "Friday";
                break;
            case Calendar.SATURDAY:
                currentDay = "Saturday";
                break;
            case Calendar.SUNDAY:
                currentDay = "Sunday";
                break;
        }

        String burnedCaloriesJson = sharedPreferences.getString("burned_calories_dict", "");
        String stepCountJson = sharedPreferences.getString("step_count_dict", "");

        Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
        HashMap<String, Integer> burnedCaloriesDict = gson.fromJson(burnedCaloriesJson, type);
        if (burnedCaloriesDict == null) burnedCaloriesDict = new HashMap<>();
        HashMap<String, Integer> stepCountDict = gson.fromJson(stepCountJson, type);
        if (stepCountDict == null) stepCountDict = new HashMap<>();

        // Update the values for the current day
        burnedCaloriesDict.put(currentDay, Math.round(caloriesBurned)); // Rounding to nearest integer
        stepCountDict.put(currentDay, currentSteps);

        // Save the updated workoutHistory list back to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        json = gson.toJson(workoutHistory);
        editor.putString("workout_history", json); // Fixed the key here
        editor.putString("burned_calories_dict", gson.toJson(burnedCaloriesDict));
        editor.putString("step_count_dict", gson.toJson(stepCountDict));
        editor.putInt("OnTraining", 1);
        editor.apply();
    }

    private void moveToStartRunningFragment() {
        StartRunningFragment startRunningFragment = new StartRunningFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, startRunningFragment);
        transaction.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (timer != null) {
            timer.cancel();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            currentHeartRate = (int) event.values[0];
            heartRateTextView.setText(String.format(Locale.getDefault(), "%.0f", currentHeartRate));
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // Increment the step count
            currentSteps++;

            // Calculate and accumulate the calories burned
            float additionalCalories = (1 / 2000f) * MET_VALUE_WALKING * 3.5f * weightInKg / 200;
            caloriesBurned += additionalCalories;

            // Update the UI
            burnedCaloriesTextView.setText(String.format(Locale.getDefault(), "%.2f", caloriesBurned));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes, if needed
    }
}
