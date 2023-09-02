package com.example.masterrunning.presentation;

public class BmiFuzzySet {
    private double underweightMembership;
    private double normalMembership;
    private double overweightMembership;
    private double obeseMembership;

    public BmiFuzzySet() {
    }

    public String calculateClassification(double bmi) {

        // Calculate memberships
        calculateMembership(bmi);

        // Return classification
        return getBmiClassification();
    }

    private void calculateMembership(double bmi) {
        if (bmi <= 18.5) {
            underweightMembership = 1.0;
        } else if (bmi <= 24.9) {
            underweightMembership = (24.9 - bmi) / (24.9 - 18.5);
        } else {
            underweightMembership = 0.0;
        }

        if (bmi <= 18.5 || bmi >= 24.9) {
            normalMembership = 0.0;
        } else {
            normalMembership = 1 - Math.abs(21.7 - bmi) / (21.7 - 18.5);
        }

        if (bmi <= 25 || bmi >= 29.9) {
            overweightMembership = 0.0;
        } else {
            overweightMembership = 1 - Math.abs(27.4 - bmi) / (27.4 - 25);
        }

        if (bmi <= 30) {
            obeseMembership = 0.0;
        } else {
            obeseMembership = 1.0;
        }
    }

    public String getBmiClassification() {
        double maxMembership = Math.max(
                Math.max(underweightMembership, normalMembership),
                Math.max(overweightMembership, obeseMembership)
        );

        if (maxMembership == underweightMembership) {
            return "Underweight";
        } else if (maxMembership == normalMembership) {
            return "Normal";
        } else if (maxMembership == overweightMembership) {
            return "Overweight";
        } else if (maxMembership == obeseMembership) {
            return "Obese";
        } else {
            return "Unknown"; // Fallback, should not happen with proper implementation
        }
    }
}
