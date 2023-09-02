package com.example.masterrunning.presentation;

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

public class AvailableDaysFragment extends Fragment {

    CheckBox mondayCheckbox;
    CheckBox tuesdayCheckbox;
    CheckBox wednesdayCheckbox;
    CheckBox thursdayCheckbox;
    CheckBox fridayCheckbox;
    CheckBox saturdayCheckbox;
    CheckBox sundayCheckbox;
    Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.available_layout, container, false);

        sundayCheckbox = ((View) view).findViewById(R.id.sundayCheckbox);
        mondayCheckbox = view.findViewById(R.id.mondayCheckbox);
        tuesdayCheckbox = view.findViewById(R.id.tuesdayCheckbox);
        wednesdayCheckbox = view.findViewById(R.id.wednesdayCheckbox);
        thursdayCheckbox = view.findViewById(R.id.thursdayCheckbox);
        fridayCheckbox = view.findViewById(R.id.fridayCheckbox);
        saturdayCheckbox = view.findViewById(R.id.saturdayCheckbox);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> selectedDays = new ArrayList<>();
                if (sundayCheckbox.isChecked()) selectedDays.add("Sunday");
                if (mondayCheckbox.isChecked()) selectedDays.add("Monday");
                if (tuesdayCheckbox.isChecked()) selectedDays.add("Tuesday");
                if (wednesdayCheckbox.isChecked()) selectedDays.add("Wednesday");
                if (thursdayCheckbox.isChecked()) selectedDays.add("Thursday");
                if (fridayCheckbox.isChecked()) selectedDays.add("Friday");
                if (saturdayCheckbox.isChecked()) selectedDays.add("Saturday");

                // Check if any day is selected
                if (!selectedDays.isEmpty()) {
                    saveData("available_days", selectedDays);

                    // Move to the next fragment
                    UserGoalsFragment usergoalsfragment = new UserGoalsFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, usergoalsfragment);
                    transaction.commit();
                } else {
                    // Show a message to select at least one day
                    Toast.makeText(getContext(), "Please select at least one available day.", Toast.LENGTH_SHORT).show();
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



}

