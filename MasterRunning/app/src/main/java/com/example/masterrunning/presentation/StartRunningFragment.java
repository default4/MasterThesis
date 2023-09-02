package com.example.masterrunning.presentation;

import static com.google.android.gms.wearable.DataMap.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class StartRunningFragment extends Fragment {
    Button btnStartRunning;
    ImageButton btnGoToStatistics;


    private Set<HealthPermissionManager.PermissionKey> mKeySet;



    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            // Code to handle successful connection to Health Data Store
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            // Code to handle failure to connect to Health Data Store
        }

        @Override
        public void onDisconnected() {
            // Code to handle disconnection from Health Data Store
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_running_layout, container, false);

        // Setting up the button for starting the running activity
        btnStartRunning = view.findViewById(R.id.btn_start_running);
        btnStartRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = startRunning();

                // Move to the next fragment
                WorkoutDetailsActivity workoutDetailsActivity = new WorkoutDetailsActivity();
                workoutDetailsActivity.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, workoutDetailsActivity);
                transaction.commit();
            }
        });

        // Setting up the button for transitioning to StatisticsFragment
        btnGoToStatistics = view.findViewById(R.id.btn_go_to_statistics); // Ensure this button exists in your layout
        btnGoToStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToStatisticsFragment();
            }
        });

        return view;
    }

    private Bundle startRunning() {
        // Get user data from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        double weight = Double.parseDouble(sharedPreferences.getString("weight", "0"));
        double height = Double.parseDouble(sharedPreferences.getString("height", "0")) / 100;  // Convert height from cm to m
        double bmi = weight / (height * height);  // Calculate BMI

        mKeySet = new HashSet<>();
        mKeySet.add(new HealthPermissionManager.PermissionKey("com.samsung.health.heart_rate", HealthPermissionManager.PermissionType.READ));
        mKeySet.add(new HealthPermissionManager.PermissionKey("com.samsung.health.sleep", HealthPermissionManager.PermissionType.READ));
        mKeySet.add(new HealthPermissionManager.PermissionKey("com.samsung.health.step_count", HealthPermissionManager.PermissionType.READ));

        // Retrieve the sleep duration in hours and v02 max values
        //double sleepHours = readSleepData();
        double sleepHours = 7;
        double vo2max = 35;


        String fitnessLevelString = sharedPreferences.getString("fitness_level", "Beginner");  // default to "Beginner" if not found

        double fitnessLevel;
        switch (fitnessLevelString) {
            case "Beginner":
                fitnessLevel = 0.0; // or any value representing Beginner
                break;
            case "Intermediate":
                fitnessLevel = 50.0; // or any value representing Intermediate
                break;
            case "Advanced":
                fitnessLevel = 100.0; // or any value representing Advanced
                break;
            default:
                fitnessLevel = 0.0;  // default value if not match
                break;
        }

        // Assuming workoutHistory and userGoalsList are Lists and are saved as JSON strings
        String workoutHistoryJson = sharedPreferences.getString("workout_history", "");
        Type workoutHistoryType = new TypeToken<List<String>>() {}.getType();
        List<String> workoutHistory = new Gson().fromJson(workoutHistoryJson, workoutHistoryType);

        String userGoalsListJson = sharedPreferences.getString("user_goals", "");
        Type userGoalsListType = new TypeToken<List<String>>() {}.getType();
        List<String> userGoalsList = new Gson().fromJson(userGoalsListJson, userGoalsListType);

        // Create a Bundle to hold all the user data
        Bundle bundle = new Bundle();
        bundle.putDouble("bmi", bmi);
        bundle.putDouble("vo2max", vo2max);
        bundle.putDouble("sleepHours", sleepHours);

        bundle.putDouble("fitnessLevel", fitnessLevel);
        bundle.putStringArrayList("workoutHistory", new ArrayList<>(workoutHistory));
        bundle.putStringArrayList("userGoalsList", new ArrayList<>(userGoalsList));

        return bundle;
    }


    private double readSleepData() {
        HealthDataStore mStore = new HealthDataStore(getContext(), mConnectionListener);
        HealthDataResolver mResolver = new HealthDataResolver(mStore, null);

        HealthDataResolver.ReadRequest request = new HealthDataResolver.ReadRequest.Builder()
                .setDataType(HealthConstants.Sleep.HEALTH_DATA_TYPE)
                .setTimeBefore(Calendar.getInstance().getTimeInMillis())
                .build();

        try {
            mResolver.read(request).setResultListener(result -> {
                double totalSleepDuration = 0.0;
                try {
                    Iterator<HealthData> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        HealthData data = iterator.next();
                        long startTime = data.getLong(HealthConstants.Sleep.START_TIME);
                        long endTime = data.getLong(HealthConstants.Sleep.END_TIME);
                        double durationHours = (endTime - startTime) / (1000.0 * 60 * 60); // Convert to hours
                        totalSleepDuration += durationHours;
                    }

                    // Here, you can add code to display or process the total sleep duration
                    Log.d(TAG, "Total Sleep Duration: " + totalSleepDuration);
                } finally {
                    result.close();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Getting sleep data fails.", e);
        }
        return 0;
    }

    private void moveToStatisticsFragment() {
        StatisticsFragment statisticsFragment = new StatisticsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, statisticsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}


