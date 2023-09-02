package com.example.masterrunning.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutHistoryFuzzySets {
    private double lowIntensityMembership;
    private double mediumIntensityMembership;
    private double highIntensityMembership;

    private List<String> workoutHistory;

    public WorkoutHistoryFuzzySets(List<String> workoutHistory) {
        this.workoutHistory = workoutHistory;
    }

    public void calculateMembership() {
        Map<String, Integer> workoutCounts = new HashMap<>();
        workoutCounts.put("Low", 0);
        workoutCounts.put("Medium", 0);
        workoutCounts.put("High", 0);

        int totalWorkouts = 0;

        for (String workout : workoutHistory) {
            if (workout.equalsIgnoreCase("Easy Run") || workout.equalsIgnoreCase("Recovery Run")) {
                workoutCounts.put("Low", workoutCounts.get("Low") + 1);
                totalWorkouts++;
            } else if (workout.equalsIgnoreCase("Progression Run") || workout.equalsIgnoreCase("Fartlek Training")) {
                workoutCounts.put("Medium", workoutCounts.get("Medium") + 1);
                totalWorkouts++;
            } else if (workout.equalsIgnoreCase("Interval Training") || workout.equalsIgnoreCase("Tempo Run") || workout.equalsIgnoreCase("Long Run")) {
                workoutCounts.put("High", workoutCounts.get("High") + 1);
                totalWorkouts++;
            }
        }

        // Apply the fuzzy membership functions here
        lowIntensityMembership = fuzzyFunction(workoutCounts.get("Low"), totalWorkouts);
        mediumIntensityMembership = fuzzyFunction(workoutCounts.get("Medium"), totalWorkouts);
        highIntensityMembership = fuzzyFunction(workoutCounts.get("High"), totalWorkouts);
    }

    private double fuzzyFunction(int workoutCount, int totalWorkouts) {
        if (totalWorkouts == 0) {
            return 0.0;
        } else {
            return workoutCount / (double) totalWorkouts;
        }
    }

    public double getLowIntensityMembership() {
        return lowIntensityMembership;
    }

    public double getMediumIntensityMembership() {
        return mediumIntensityMembership;
    }

    public double getHighIntensityMembership() {
        return highIntensityMembership;
    }

    public List<String> getLastTwoWorkouts() {
        return workoutHistory.subList(workoutHistory.size() - 2, workoutHistory.size());
    }

    public List<String> getLastThreeWorkouts() {
        return workoutHistory.subList(workoutHistory.size() - 3, workoutHistory.size());
    }
}
