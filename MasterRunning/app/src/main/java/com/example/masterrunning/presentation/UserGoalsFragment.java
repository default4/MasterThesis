package com.example.masterrunning.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class UserGoalsFragment extends Fragment {

    CheckBox improveOverallFitnessCheckbox;
    CheckBox weightLossCheckbox;
    CheckBox increaseRunningSpeedCheckbox;
    CheckBox improveRunningEnduranceCheckbox;
    Button nextButton;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goals_layout, container, false);

        improveOverallFitnessCheckbox = view.findViewById(R.id.checkBoxImproveOverallFitness);
        weightLossCheckbox = view.findViewById(R.id.checkBoxWeightLoss);
        increaseRunningSpeedCheckbox = view.findViewById(R.id.checkBoxIncreaseRunningSpeed);
        improveRunningEnduranceCheckbox = view.findViewById(R.id.checkBoxImproveRunningEndurance);

        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedGoals = new ArrayList<>();
                if (improveOverallFitnessCheckbox.isChecked()) selectedGoals.add("Improve Overall Fitness");
                if (weightLossCheckbox.isChecked()) selectedGoals.add("Weight Loss");
                if (increaseRunningSpeedCheckbox.isChecked()) selectedGoals.add("Increase Running Speed");
                if (improveRunningEnduranceCheckbox.isChecked()) selectedGoals.add("Improve Running Endurance");

                // Check if any goal is selected
                if (!selectedGoals.isEmpty()) {
                    saveData("user_goals", selectedGoals);


                    // Initialize "Workout History" with 7 "Rest" entries
                    ArrayList<String> workoutHistory = new ArrayList<>(Collections.nCopies(7, "Rest"));

                    ArrayList<Integer> sleepHistory = new ArrayList<>(Collections.nCopies(7, 0));
                    ArrayList<Integer> caloriesHistory = new ArrayList<>(Collections.nCopies(7, 0));
                    ArrayList<Integer> stepsHistory = new ArrayList<>(Collections.nCopies(7, 0));
                    ArrayList<Integer> kilosHistory = new ArrayList<>(Collections.nCopies(7, 0));

                    saveData("workout_history", workoutHistory);

                    saveDataInt("sleep_history", sleepHistory);
                    saveDataInt("calorie_history", caloriesHistory);
                    saveDataInt("steps_history", stepsHistory);
                    saveDataInt("kilos_history", kilosHistory);

                    saveIntData("OnTraining", 0);


                    HashMap<String, Integer> sleepDict = new HashMap<>();
                    HashMap<String, Integer> burnedCaloriesDict = new HashMap<>();
                    HashMap<String, Integer> stepCountDict = new HashMap<>();
                    HashMap<String, Integer> kilometerDict = new HashMap<>();

                    String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

                    for (String day : weekdays) {
                        sleepDict.put(day, 0);
                        burnedCaloriesDict.put(day, 0);
                        stepCountDict.put(day, 0);
                        kilometerDict.put(day, 0);
                    }


                    saveMapData("sleep_dict", sleepDict);
                    saveMapData("burned_calories_dict", burnedCaloriesDict);
                    saveMapData("steps_count_dict", stepCountDict);
                    saveMapData("kilometers_count_dict", kilometerDict);

                    // Navigate to next screen
                    StartRunningFragment startrunningfragment = new StartRunningFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, startrunningfragment);
                    transaction.commit();
                } else {
                    // Show a message to select at least one goal
                    Toast.makeText(getContext(), "Please select at least one goal.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void saveData(String key, ArrayList<String> list) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    private void saveDataInt(String key, ArrayList<Integer> list) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    private void saveIntData(String key, int value) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private void saveMapData(String key, HashMap<String, Integer> map) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(map);
        editor.putString(key, json);
        editor.apply();
    }
}

