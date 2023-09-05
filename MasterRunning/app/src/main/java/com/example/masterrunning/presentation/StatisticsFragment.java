package com.example.masterrunning.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class StatisticsFragment extends Fragment {

    private TextView currentDayView, todayStepsView, todayCaloriesView, avgStepsView, avgCaloriesView, numberOfTrainingsView;
    private LineChart caloriesLineChart;
    private LineChart stepsLineChart;
    private LineChart trainingsLineChart;

    @SuppressLint("MissingInflatedId")
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

        // Initialize LineCharts
        caloriesLineChart = view.findViewById(R.id.caloriesLineChart);
        stepsLineChart = view.findViewById(R.id.stepsLineChart);


        // Populate the charts
        populateCaloriesChart();
        populateStepsChart();



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
        String stepsDictString = sharedPreferences.getString("steps_count_dict", "");
        String caloriesDictString = sharedPreferences.getString("burned_calories_dict", "");

        String stepsToday = getValueFromDict(stepsDictString, currentDay);
        String caloriesToday = getValueFromDict(caloriesDictString, currentDay);

        // Display today's steps and calories
        todayStepsView.setText("Today Steps:" + (stepsToday != null ? stepsToday : "0"));
        todayCaloriesView.setText("Today's Cals:" + (caloriesToday != null ? caloriesToday : "0"));

        // Calculate and display average values
        avgStepsView.setText("Wkly Avg Steps:" + calculateWeeklyAverage(stepsDictString));
        avgCaloriesView.setText("Wkly Avg Cals:" + calculateWeeklyAverage(caloriesDictString));

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
        numberOfTrainingsView.setText("Wkly Trainings:" + trainingCount);
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

    private void populateCaloriesChart() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String calorieHistoryJson = sharedPreferences.getString("calorie_history", "");
        Gson gson = new Gson();
        Type type = new TypeToken<int[]>(){}.getType();
        int[] weeklyCalories = gson.fromJson(calorieHistoryJson, type);
        if (weeklyCalories == null) weeklyCalories = new int[0];

        // Reverse the array
        int[] reversedCalories = new int[weeklyCalories.length];
        for (int i = 0; i < weeklyCalories.length; i++) {
            reversedCalories[i] = weeklyCalories[weeklyCalories.length - 1 - i];
        }

        ArrayList<Entry> entries = new ArrayList<>();
        int minCalories = Integer.MAX_VALUE;
        int maxCalories = Integer.MIN_VALUE;

        for (int i = 0; i < reversedCalories.length; i++) {
            entries.add(new Entry(i, reversedCalories[i]));
            minCalories = Math.min(minCalories, reversedCalories[i]);
            maxCalories = Math.max(maxCalories, reversedCalories[i]);
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.GREEN);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        caloriesLineChart.setData(lineData);

        styleChart(caloriesLineChart, "Calories");

        // Dynamic y-axis scaling
        caloriesLineChart.getAxisLeft().setAxisMinimum(minCalories - 5);  // A bit lower than the minimum value
        caloriesLineChart.getAxisLeft().setAxisMaximum(maxCalories + 5);  // A bit higher than the maximum value

        // Dynamic y-axis scaling
        if (minCalories >= 0) {
            caloriesLineChart.getAxisLeft().setAxisMinimum(0);  // Set minimum to zero
        } else {
            caloriesLineChart.getAxisLeft().setAxisMinimum(minCalories - 5);  // A bit lower than the minimum value
        }
        caloriesLineChart.getAxisLeft().setAxisMaximum(maxCalories + 5);  // A bit higher than the maximum value

        caloriesLineChart.invalidate();
    }

    private void populateStepsChart() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String stepsHistoryJson = sharedPreferences.getString("steps_history", "");
        Gson gson = new Gson();
        Type type = new TypeToken<int[]>(){}.getType();
        int[] weeklySteps = gson.fromJson(stepsHistoryJson, type);
        if (weeklySteps == null) weeklySteps = new int[0];

        // Reverse the array
        int[] reversedSteps = new int[weeklySteps.length];
        for (int i = 0; i < weeklySteps.length; i++) {
            reversedSteps[i] = weeklySteps[weeklySteps.length - 1 - i];
        }

        ArrayList<Entry> entries = new ArrayList<>();
        int minSteps = Integer.MAX_VALUE;
        int maxSteps = Integer.MIN_VALUE;

        for (int i = 0; i < reversedSteps.length; i++) {
            entries.add(new Entry(i, reversedSteps[i]));
            minSteps = Math.min(minSteps, reversedSteps[i]);
            maxSteps = Math.max(maxSteps, reversedSteps[i]);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Steps Taken");
        dataSet.setColor(Color.GREEN);  // Line color
        dataSet.setValueTextColor(Color.WHITE);  // Value text color
        LineData lineData = new LineData(dataSet);
        stepsLineChart.setData(lineData);

        // Dynamic y-axis scaling
        stepsLineChart.getAxisLeft().setAxisMinimum(minSteps - 5);  // A bit lower than the minimum value
        stepsLineChart.getAxisLeft().setAxisMaximum(maxSteps + 5);  // A bit higher than the maximum value

        styleChart(stepsLineChart, "Steps");  // Apply the styling

        // Dynamic y-axis scaling
        if (minSteps >= 0) {
            stepsLineChart.getAxisLeft().setAxisMinimum(0);  // Set minimum to zero
        } else {
            stepsLineChart.getAxisLeft().setAxisMinimum(minSteps - 5);  // A bit lower than the minimum value
        }
        stepsLineChart.getAxisLeft().setAxisMaximum(maxSteps + 5);  // A bit higher than the maximum value

        stepsLineChart.invalidate(); // Refresh the chart
    }

    private void styleChart(LineChart chart, String title) {
        chart.setBackgroundColor(Color.TRANSPARENT);  // Transparent background
        chart.setDrawGridBackground(false);

        // Enable and set the color of the border
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.WHITE);
        chart.setBorderWidth(2f);  // Set the border width

        chart.setDrawMarkers(false);
        chart.getLegend().setEnabled(false);

        // Set the description (title) at the top of the chart
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(title);
        chart.getDescription().setTextColor(Color.WHITE);  // White text
        chart.getDescription().setTextSize(12f);  // Adjust text size as needed
        chart.getDescription().setPosition(chart.getWidth() / 2, 20f);  // Position at the top

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisLeft().setTextColor(Color.WHITE);  // White text
        chart.getAxisLeft().setAxisMinimum(0);  // No negative numbers
        chart.getAxisLeft().setAxisMaximum(250);
        chart.getAxisLeft().setLabelCount(5, true);  // Show 5 labels

        // Enable and customize the x-axis
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // Position at the bottom
        chart.getXAxis().setTextColor(Color.WHITE);  // White text
        chart.getXAxis().setLabelCount(7, true);  // 7 weeks
        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + (int) (value + 1);
            }
        });
    }

    // Custom formatter for the X-axis labels
    public class WeekAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return "Week " + (int) (value + 1);
        }
    }

}
