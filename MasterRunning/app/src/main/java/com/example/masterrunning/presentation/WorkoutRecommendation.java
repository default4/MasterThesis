package com.example.masterrunning.presentation;

import java.io.Serializable;

public class WorkoutRecommendation implements Serializable {
    private String type;
    private String duration;
    private String intensity;

    public WorkoutRecommendation(String type, String intensity, String duration ) {
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
    }

    public String getType() {
        return type;
    }

    public String getDuration() {
        return duration;
    }

    public String getIntensity() {
        return intensity;
    }

    @Override
    public String toString() {
        return "Workout Recommendation: " +
                "type='" + type + '\'' +
                ", duration='" + duration + '\'' +
                ", intensity='" + intensity + '\'';
    }
}
