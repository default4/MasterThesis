package com.example.masterrunning.presentation;

public class Vo2MaxFuzzySet {
    private double poorMembership;
    private double fairMembership;
    private double goodMembership;
    private double excellentMembership;

    public Vo2MaxFuzzySet() {
    }

    public String calculateClassification(double vo2Max) {
        // Calculate memberships
        calculateMembership(vo2Max);

        // Return classification
        return getVo2MaxClassification();
    }

    private void calculateMembership(double vo2Max) {
        // Adjust these thresholds as needed
        if (vo2Max <= 20) {
            poorMembership = 1.0;
        } else if (vo2Max <= 30) {
            poorMembership = (30 - vo2Max) / (30 - 20);
        } else {
            poorMembership = 0.0;
        }

        if (vo2Max <= 20 || vo2Max >= 40) {
            fairMembership = 0.0;
        } else {
            fairMembership = 1 - Math.abs(30 - vo2Max) / (30 - 20);
        }

        if (vo2Max <= 40 || vo2Max >= 50) {
            goodMembership = 0.0;
        } else {
            goodMembership = 1 - Math.abs(45 - vo2Max) / (45 - 40);
        }

        if (vo2Max <= 50) {
            excellentMembership = 0.0;
        } else {
            excellentMembership = 1.0;
        }
    }

    public String getVo2MaxClassification() {
        double maxMembership = Math.max(
                Math.max(poorMembership, fairMembership),
                Math.max(goodMembership, excellentMembership)
        );

        if (maxMembership == poorMembership) {
            return "Poor";
        } else if (maxMembership == fairMembership) {
            return "Fair";
        } else if (maxMembership == goodMembership) {
            return "Good";
        } else if (maxMembership == excellentMembership) {
            return "Excellent";
        } else {
            return "Unknown"; // Fallback, should not happen with proper implementation
        }
    }
}
