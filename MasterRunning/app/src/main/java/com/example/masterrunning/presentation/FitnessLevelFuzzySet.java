package com.example.masterrunning.presentation;

public class FitnessLevelFuzzySet {
    private double beginnerMembership;
    private double intermediateMembership;
    private double advancedMembership;

    public FitnessLevelFuzzySet() {
    }

    public void calculateMembership(double fitnessLevel) {
        // For 'Beginner'
        if (fitnessLevel <= 0) {
            beginnerMembership = 1.0;
        } else if (fitnessLevel <= 50) {
            beginnerMembership = (50 - fitnessLevel) / 50;
        } else {
            beginnerMembership = 0.0;
        }

        // For 'Intermediate'
        if (fitnessLevel <= 0 || fitnessLevel >= 100) {
            intermediateMembership = 0.0;
        } else if (fitnessLevel <= 50) {
            intermediateMembership = fitnessLevel / 50;
        } else {
            intermediateMembership = (100 - fitnessLevel) / 50;
        }

        // For 'Advanced'
        if (fitnessLevel >= 100) {
            advancedMembership = 1.0;
        } else if (fitnessLevel >= 50) {
            advancedMembership = (fitnessLevel - 50) / 50;
        } else {
            advancedMembership = 0.0;
        }
    }

    public double getBeginnerMembership() {
        return beginnerMembership;
    }

    public double getIntermediateMembership() {
        return intermediateMembership;
    }

    public double getAdvancedMembership() {
        return advancedMembership;
    }

    public String getMainFitnessLevel() {
        if (beginnerMembership >= intermediateMembership && beginnerMembership >= advancedMembership) {
            return "Beginner";
        } else if (intermediateMembership >= beginnerMembership && intermediateMembership >= advancedMembership) {
            return "Intermediate";
        } else {
            return "Advanced";
        }
    }
}