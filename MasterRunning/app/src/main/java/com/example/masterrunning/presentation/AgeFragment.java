package com.example.masterrunning.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.masterrunning.R;

import java.lang.reflect.Field;

public class AgeFragment extends Fragment {

    private NumberPicker agePicker;
    private Button nextButton;
    private int selectedAge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.age_layout, container, false);


        // Initialize views
        agePicker = view.findViewById(R.id.agePicker);
        nextButton = view.findViewById(R.id.nextButton);

        // Set the minimum and maximum values for the NumberPicker
        agePicker.setMinValue(10);
        agePicker.setMaxValue(100);

        // Set onClickListener for the next button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedAge = agePicker.getValue();
                saveData("age", Integer.toString(selectedAge));

                // Move to the next fragment
                WeightFragment weightFragment = new WeightFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, weightFragment);
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
