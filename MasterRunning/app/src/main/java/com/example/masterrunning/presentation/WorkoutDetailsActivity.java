package com.example.masterrunning.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.masterrunning.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WorkoutDetailsActivity extends Fragment {
    FuzzyFitnessAdvisor fuzzyFitnessAdvisor;

    public WorkoutDetailsActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_workout_details, container, false);

        // Get user data from bundle arguments
        Bundle bundle = getArguments();
        double bmi = bundle.getDouble("bmi");
        double vo2max = bundle.getDouble("vo2max");
        double sleepHours = bundle.getDouble("sleepHours");
        int fitnessLevel = bundle.getInt("fitnessLevel");
        List<String> workoutHistory = bundle.getStringArrayList("workoutHistory");
        List<String> userGoalsList = bundle.getStringArrayList("userGoalsList");

        fuzzyFitnessAdvisor = new FuzzyFitnessAdvisor(getContext());

        // Load the user data
        fuzzyFitnessAdvisor.loadUserData(bmi, vo2max, sleepHours, fitnessLevel, workoutHistory, userGoalsList);

        // Call recommendWorkout
        WorkoutRecommendation workout = fuzzyFitnessAdvisor.recommendWorkout();

        String durationDisplay = workout.getDuration();
        String intensityDisplay = workout.getIntensity();
        String workoutType = workout.getType();

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        double age = Double.parseDouble(sharedPreferences.getString("age", "0"));
        String gender = sharedPreferences.getString("gender", "Male");

        double MHR;
        double prozent;
        int progStart;
        int progEnd;
        String description = "";
        double intensityDisplayValue;

        if (gender == "Male") {  // Male
            MHR = 214 - (0.8 * age);
        } else {  // Female
            MHR = 209 - (0.7 * age);
        }

        switch (workout.getType()) {
            case "Long Run":
                if ("low".equals(intensityDisplay)) {

                    prozent = ThreadLocalRandom.current().nextDouble(65, 75 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "60";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "75";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "110";
                    }

                    description = "Jog easily. Aim to cover a longer distance over " + durationDisplay + " minutes. Keep heart rate between " + intensityDisplay + " bpm";
                }


                if ("moderate".equals(intensityDisplay) || "high".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(75, 85 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "45 minutes";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "60 minutes";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "75 minutes";
                    }

                    description = "Pick up the pace but remain comfortable. Run for " + durationDisplay + " minutes at " + intensityDisplay + " bpm";
                }
                break;

            case "Interval Training":
                if ("low".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(60, 70 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "Run short bursts at " + intensityDisplay + " Run short bursts at " + intensityDisplay + " bpm, then walk or jog to recover. Repeat for " + durationDisplay + " minutes total. Do 8 intervals of 2 minutes each with 1 minute rests.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "25";
                        description = "Run short bursts at " + intensityDisplay + " bpm, then walk or jog to recover. Repeat for " + durationDisplay + " minutes total. Do 10 intervals of 2 minutes each with 1 minute rests.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Run short bursts at " + intensityDisplay + " bpm, then walk or jog to recover. Repeat for " + durationDisplay + " minutes total. Do 12 intervals of 2 minutes each with 1 minute rests.";
                    }
                }

                if ("moderate".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(75, 85 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "45";
                        description = "Run harder bursts at " + intensityDisplay + " bpm, with walking or jogging in between. Total workout should be " + durationDisplay + " minutes total. Do 8 intervals of 2.5 minutes each with 1-1.5 minute rests.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "60";
                        description = "Run harder bursts at " + intensityDisplay + " bpm, with walking or jogging in between. Total workout should be " + durationDisplay + " minutes total. Do 10 intervals of 3 minutes each with 1.5 minute rests.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "75";
                        description = "Run harder bursts at " + intensityDisplay + " bpm, with walking or jogging in between. Total workout should be " + durationDisplay + " minutes total. Do 12 intervals of 3.5 minutes each with 1.5-2 minute rests.";
                    }
                }

                if ("high".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(75, 85 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "18";
                        description = "Sprint nearly all-out for short periods at " + intensityDisplay + " bpm, then rest. Keep session " + durationDisplay + " minutes total. Do 6 intervals of 2 minutes each with 1 minute rests.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "Sprint nearly all-out for short periods at " + intensityDisplay + " bpm, then rest. Keep session " + durationDisplay + " minutes total. Do 8 intervals of 2 minutes each with 1-1.5 minute rests.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "35";
                        description = "Sprint nearly all-out for short periods at " + intensityDisplay + " bpm, then rest. Keep session " + durationDisplay + " minutes total. Do 10 intervals of 2.5 minutes each with 1.5 minute rests.";
                    }
                }

                break;

            case "Progression Run":
                if ("low".equals(intensityDisplay)) {

                    progStart = 50;
                    progEnd = 70;
                    double intensityDisplayValueStart = (progStart / 100) * MHR;
                    double intensityDisplayValueEnd = (progEnd / 100) * MHR;

                    intensityDisplayValueStart = (int) intensityDisplayValueStart;
                    intensityDisplayValueEnd = (int) intensityDisplayValueEnd;

                    String intensityDisplayStart = String.valueOf(intensityDisplayValueStart);
                    String intensityDisplayEnd = String.valueOf(intensityDisplayValueEnd);

                    intensityDisplay = intensityDisplayStart + "-" + intensityDisplayEnd;

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "45";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "55";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "60";
                    }

                    description = "Start slow and gradually speed up, moving from " + intensityDisplayStart + " bpm to " + intensityDisplayEnd + " bpm over " + durationDisplay + " minutes";
                }

                if ("moderate".equals(intensityDisplay)) {
                    progStart = 60;
                    progEnd = 80;
                    double intensityDisplayValueStart = (progStart / 100) * MHR;
                    double intensityDisplayValueEnd = (progEnd / 100) * MHR;

                    intensityDisplayValueStart = (int) intensityDisplayValueStart;
                    intensityDisplayValueEnd = (int) intensityDisplayValueEnd;

                    String intensityDisplayStart = String.valueOf(intensityDisplayValueStart);
                    String intensityDisplayEnd = String.valueOf(intensityDisplayValueEnd);

                    intensityDisplay = intensityDisplayStart + "-" + intensityDisplayEnd;

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "35";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "40";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "45";
                    }

                    description = "Begin at a steady pace and accelerate, transitioning from " + intensityDisplayStart + " bpm to " + intensityDisplayEnd + " bpm over " + durationDisplay + " minutes";
                }

                if ("high".equals(intensityDisplay)) {
                    progStart = 70;
                    progEnd = 90;
                    double intensityDisplayValueStart = (progStart / 100) * MHR;
                    double intensityDisplayValueEnd = (progEnd / 100) * MHR;

                    intensityDisplayValueStart = (int) intensityDisplayValueStart;
                    intensityDisplayValueEnd = (int) intensityDisplayValueEnd;

                    String intensityDisplayStart = String.valueOf(intensityDisplayValueStart);
                    String intensityDisplayEnd = String.valueOf(intensityDisplayValueEnd);

                    intensityDisplay = intensityDisplayStart + "-" + intensityDisplayEnd;

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "25";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "30";
                    }

                    description = "Kick off briskly and ramp up to near-max pace, shifting from " + intensityDisplayStart + " bpm to " + intensityDisplayEnd + " bpm over " + durationDisplay + " minutes";
                }

                break;

            case "Fartlek Training":
                if ("low".equals(intensityDisplay)) {

                    prozent = ThreadLocalRandom.current().nextDouble(60, 70 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "After a 5-minute warm-up, mix in 3-4 short bursts of increased pace " + intensityDisplay + " bmp for about 1 minute each, interspersed with easy running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "After a 5-minute warm-up, mix in 5-6 bursts of increased pace " + intensityDisplay + " bmp for about 1-2 minute each, interspersed with easy running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "After a 5-minute warm-up, mix in 7-8 bursts of increased pace " + intensityDisplay + " bmp for about 2-minute each, interspersed with easy running. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                if ("moderate".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(75, 85 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "25";
                        description = "After warming up, include 4-5 bursts of 2 minutes at a faster pace " + intensityDisplay + " bmp followed by 2 minutes of easy running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "35";
                        description = "Begin with a warm-up, then incorporate 5-6 bursts of 2-3 minutes at an increased pace " + intensityDisplay + " bmp, separated by 2-3 minutes of easy running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "50";
                        description = "Warm-up, then mix in 7-8 bursts of 3 minutes at a quicker pace " + intensityDisplay + " bmp, interspersed with 3 minutes of relaxed running. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                if ("high".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(90, 95 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "After a warm-up, do 4-5 sprints of 1 minute each at " + intensityDisplay + " bmp, separated by 2-3 minutes of relaxed running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Warm-up first, then mix in 5-6 sprints of 1.5 minutes each at " + intensityDisplay + " bmp, interspersed with 2-3 minutes of easy running. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "Begin with a warm-up, followed by 7-8 sprints of 2 minutes each at " + intensityDisplay + " bmp, separated by 2-3 minutes of relaxed running. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                break;

            case "Tempo Run":
                if ("low".equals(intensityDisplay)) {

                    prozent = ThreadLocalRandom.current().nextDouble(65, 75 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "After a 5-minute easy-paced warm-up, maintain a steady pace for 15 minutes at " + intensityDisplay + " bmp, then cool down for 5 minutes. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Begin with a 5-minute warm-up, maintain your tempo for 20 minutes at " + intensityDisplay + " bmp, and finish with a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "Start with a 5-minute warm-up, keep the tempo for 30 minutes at " + intensityDisplay + " bmp, and conclude with a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                if ("moderate".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(75, 85 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "25";
                        description = "Warm-up for 5 minutes, maintain the set pace for 15 minutes at " + intensityDisplay + " bmp, and then cool down for 5 minutes. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "35";
                        description = "Start with a 5-minute warm-up, hold the tempo for 25 minutes at " + intensityDisplay + " bmp, , and finish with a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "50";
                        description = "After a 5-minute warm-up, maintain the tempo for 40 minutes at " + intensityDisplay + " bmp, then conclude with a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                if ("high".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(85, 90 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "Warm-up for 5 minutes, then push yourself for 10 minutes at high intensity at " + intensityDisplay + " bmp, and cool down for 5 minutes. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Start with a 5-minute warm-up, then hold a challenging pace for 20 minutes at " + intensityDisplay + " bmp, followed by a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "Begin with a 5-minute warm-up, keep the high tempo for 30 minutes at " + intensityDisplay + " bmp, and conclude with a 5-minute cool-down. Keep session " + durationDisplay + " minutes total.";
                    }
                }

                break;

            case "Easy Run":
                if ("low".equals(intensityDisplay)) {

                    prozent = ThreadLocalRandom.current().nextDouble(50, 60 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "Maintain a relaxed and comfortable pace throughout. This is more about staying active than pushing hard.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Keep a consistent, easy pace. Think of this as a conversational run, where you can chat without getting out of breath.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "Continue with a steady, relaxed pace. Ensure that you're not exerting yourself – it's about the time on your feet.";
                    }
                }

                if ("moderate".equals(intensityDisplay) || "high".equals(intensityDisplay)) {
                    prozent = ThreadLocalRandom.current().nextDouble(60, 70 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "25";
                        description = "Slightly up the tempo from the low intensity but still keep it relatively relaxed.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "35";
                        description = "Find a comfortable rhythm that's a tad faster than the low-intensity run but still allows for a chat.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "50";
                        description = "The idea is to run longer without pushing hard. Maintain a pace where you're working, but not overstraining.";
                    }
                }

                break;

            case "Recovery Run":
                if ("low".equals(intensityDisplay)) {

                    prozent = ThreadLocalRandom.current().nextDouble(50, 60 + 1);
                    intensityDisplayValue = (prozent / 100) * MHR;
                    intensityDisplayValue = (int) intensityDisplayValue;
                    intensityDisplay = String.valueOf(intensityDisplayValue);

                    if ("short".equals(durationDisplay)) {
                        durationDisplay = "20";
                        description = "Adopt a gentle, relaxed pace. This run is all about recovery, so there shouldn't be any strain. It's a great time to focus on your form and breathing.";
                    }

                    if ("moderate".equals(durationDisplay)) {
                        durationDisplay = "30";
                        description = "Keep your pace easy and consistent. Allow your muscles to loosen up, helping to promote recovery and reduce muscle soreness.";
                    }

                    if ("long".equals(durationDisplay)) {
                        durationDisplay = "40";
                        description = "Continue with a gentle pace, concentrating on each step and ensuring you're not pushing yourself. The goal is rejuvenation, not exertion.";
                    }
                }

                break;

            case "Rest":
                durationDisplay = "0";
                intensityDisplay = "0";
                description = "Today is a rest day. No workouts are planned. Take this time to recover.";
                break;


            default:
                break;
        }

        // Find the button using its ID
        Button startButton = view.findViewById(R.id.startTrainingButton);

        // Set an OnClickListener to the button
        String finalDurationDisplay = durationDisplay;
        String finalIntensityDisplay = intensityDisplay;
        String finalTypeDisplay = workoutType;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an intent to start the next activity
                Intent intent = new Intent(getActivity(), RunningProcess.class);

                // Pass durationDisplay and intensityDisplay as extras
                intent.putExtra("duration", finalDurationDisplay);
                intent.putExtra("intensity", finalIntensityDisplay);
                intent.putExtra("type", finalTypeDisplay);

                // Start the next activity
                startActivity(intent);

                if ("Rest".equals(workoutType)) {
                    if (getFragmentManager() != null) {
                        getFragmentManager().popBackStack();
                        return;
                    }
                }


            }
        });

        // Display the workout details
        TextView textViewWorkoutName = view.findViewById(R.id.workoutName);
        textViewWorkoutName.setText(workout.getType());

        TextView textViewDurationText = view.findViewById(R.id.durationText);
        textViewDurationText.setText("Duration: " + durationDisplay + " minutes");

        TextView textViewIntensityText = view.findViewById(R.id.intensityText);
        textViewIntensityText.setText("Intensity: " + intensityDisplay + " bpm");

        TextView textViewWorkoutDescription = view.findViewById(R.id.workoutDescription);
        textViewWorkoutDescription.setText(description);

        return view;
    }
}
