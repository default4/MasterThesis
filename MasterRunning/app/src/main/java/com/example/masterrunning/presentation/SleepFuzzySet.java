package com.example.masterrunning.presentation;

public class SleepFuzzySet {
    private double inadequateSleepMembership;
    private double adequateSleepMembership;
    private double excellentSleepMembership;

    public SleepFuzzySet() {
    }

    public void calculateMembership(double sleepHours) {
        if (sleepHours <= 6) {
            inadequateSleepMembership = 1.0;
        } else {
            inadequateSleepMembership = 0.0;
        }

        if (sleepHours < 6 || sleepHours > 8) {
            adequateSleepMembership = 0.0;
        } else {
            adequateSleepMembership = (8 - sleepHours) / (8 - 6);
        }

        if (sleepHours <= 8) {
            excellentSleepMembership = 0.0;
        } else {
            excellentSleepMembership = 1.0;
        }
    }

    public String getSleepClassification() {
        double maxMembership = Math.max(inadequateSleepMembership, Math.max(adequateSleepMembership, excellentSleepMembership));

        if (maxMembership == inadequateSleepMembership) {
            return "Inadequate";
        } else if (maxMembership == adequateSleepMembership) {
            return "Adequate";
        } else if (maxMembership == excellentSleepMembership) {
            return "Excellent";
        } else {
            return "Unknown";
        }
    }
}
