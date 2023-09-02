package com.example.masterrunning.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;


public class FitnessLevelFragment extends Fragment {

    private RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fitbess_layout, container, false);

        // Initialize views
        radioGroup = view.findViewById(R.id.fitnessLevelGroup);
        Button nextButton = view.findViewById(R.id.nextButton);

        // Set onClickListener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedLevelId = radioGroup.getCheckedRadioButtonId();
                // check if a RadioButton is selected
                if (selectedLevelId != -1) {
                    RadioButton selectedLevelButton = view.findViewById(selectedLevelId);
                    String selectedLevel = selectedLevelButton.getText().toString();

                    saveData("fitness_level", selectedLevel);

                    // Move to the next fragment
                    // You'll need to implement this based on your app's requirements

                    AvailableDaysFragment availableDaysFragment = new AvailableDaysFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, availableDaysFragment);
                    transaction.commit();

                } else {
                    // Show a Toast if no fitness level is selected
                    Toast.makeText(getContext(), "Please select a fitness level", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void saveData(String key, String value) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
