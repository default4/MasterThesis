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

public class WeightFragment extends Fragment {

    private NumberPicker weightPicker;
    private Button nextButton;
    private int selectedWeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.weight_layout, container, false);

        // Initialize views
        weightPicker = view.findViewById(R.id.weightPicker);
        nextButton = view.findViewById(R.id.nextButton);

        // Set the minimum and maximum values for the NumberPicker
        weightPicker.setMinValue(40);
        weightPicker.setMaxValue(200);

        // Set onClickListener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedWeight = weightPicker.getValue();
                saveData("weight", Integer.toString(selectedWeight));

                // Move to the next fragment
                HeightFragment heightFragment = new HeightFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, heightFragment);
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
