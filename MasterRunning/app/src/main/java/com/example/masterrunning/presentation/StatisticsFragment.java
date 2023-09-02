package com.example.masterrunning.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private TextView currentDayView, todayStepsView, todayCaloriesView, avgStepsView, avgCaloriesView, numberOfTrainingsView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Initialize views
        currentDayView = view.findViewById(R.id.current_day);
        todayStepsView = view.findViewById(R.id.today_steps);
        todayCaloriesView = view.findViewById(R.id.today_calories);
        avgStepsView = view.findViewById(R.id.avg_steps);
        avgCaloriesView = view.findViewById(R.id.avg_calories);
        numberOfTrainingsView = view.findViewById(R.id.number_of_trainings);

        // Update the data on views
        updateStatisticsData();


        Button backButton = view.findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back or pop this fragment from the stack
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }

    private void updateStatisticsData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        // Set the current day
        String currentDay = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        currentDayView.setText(currentDay);

        // Get steps and calories data
        String stepsDictString = sharedPreferences.getString("step_count_dict", "");
        String caloriesDictString = sharedPreferences.getString("burned_calories_dict", "");

        String stepsToday = getValueFromDict(stepsDictString, currentDay);
        String caloriesToday = getValueFromDict(caloriesDictString, currentDay);

        // Display today's steps and calories
        todayStepsView.setText("Today's Steps:\n" + (stepsToday != null ? stepsToday : "0"));
        todayCaloriesView.setText("Today's Burned Calories:\n" + (caloriesToday != null ? caloriesToday : "0"));

        // Calculate and display average values
        avgStepsView.setText("Average Steps This Week:\n" + calculateWeeklyAverage(stepsDictString));
        avgCaloriesView.setText("Average Burned Calories This Week:\n" + calculateWeeklyAverage(caloriesDictString));

        // Calculate and display number of trainings
        String workoutHistoryJson = sharedPreferences.getString("workout_history", "");
        Type workoutHistoryType = new TypeToken<List<String>>() {}.getType();
        List<String> workoutHistory = new Gson().fromJson(workoutHistoryJson, workoutHistoryType);

        int trainingCount = 0;
        for (String workout : workoutHistory) {
            if (!workout.equals("Rest")) {
                trainingCount++;
            }
        }
        numberOfTrainingsView.setText("Number of Trainings This Week:\n" + trainingCount);
    }
    private String getValueFromDict(String jsonString, String key) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(jsonString, type);
        return map.get(key);
    }


    private int calculateWeeklyAverage(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        Map<String, Integer> map = gson.fromJson(jsonString, type);

        int total = 0;
        for (Integer value : map.values()) {
            total += value;
        }
        return total / 7;  // Assuming you always have 7 days worth of data
    }

}
