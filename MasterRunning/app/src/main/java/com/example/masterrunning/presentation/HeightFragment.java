package com.example.masterrunning.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;

public class HeightFragment extends Fragment {

    private NumberPicker heightPicker;
    private Button nextButton;
    private int selectedHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.height_layout, container, false);

        // Initialize views
        heightPicker = view.findViewById(R.id.heightPicker);
        nextButton = view.findViewById(R.id.nextButton);

        // Set the minimum and maximum values for the NumberPicker
        heightPicker.setMinValue(100);
        heightPicker.setMaxValue(220);

        // Set onClickListener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the selected height
                selectedHeight = heightPicker.getValue();
                saveData("height", String.valueOf(selectedHeight));

                // Navigate to the next screen
                // You'll need to implement this based on your app's requirements

                // Move to the next fragment
                FitnessLevelFragment fitnessLevelFragment = new FitnessLevelFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fitnessLevelFragment);
                transaction.commit();
            }
        });

        return view;
    }

    private void saveData(String key, String value) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
