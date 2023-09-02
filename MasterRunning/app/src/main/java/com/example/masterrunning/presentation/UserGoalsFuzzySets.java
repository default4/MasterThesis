package com.example.masterrunning.presentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class UserGoalsFuzzySets {

    private List<String> userGoals;
    private Map<String, Double> memberships;

    public UserGoalsFuzzySets(List<String> userGoals) {
        this.userGoals = userGoals != null ? userGoals : new ArrayList<>();
        this.memberships = new HashMap<>();
    }

    public void calculateMemberships() {
        List<String> possibleGoals = Arrays.asList("Improve Overall Fitness", "Increase Running Speed", "Weight Loss", "Improve Running Endurance");

        // For this example, let's say the importance of each goal is determined by its index in the list.
        // You can replace this with your own logic for determining the main goal.
        double membershipValue = 1.0;
        double decrement = 1.0 / possibleGoals.size();

        for (String goal : possibleGoals) {
            if (userGoals.contains(goal)) {
                memberships.put(goal, membershipValue);
                membershipValue -= decrement; // Decrement the membership value for the next goal
            } else {
                memberships.put(goal, 0.0);
            }
        }
    }

    public String getMainGoal() {
        String mainGoal = null;
        double maxMembership = -1.0;

        for (Map.Entry<String, Double> entry : memberships.entrySet()) {
            if (entry.getValue() > maxMembership) {
                maxMembership = entry.getValue();
                mainGoal = entry.getKey();
            }
        }

        return mainGoal;
    }
}
