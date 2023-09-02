package com.example.masterrunning.presentation;

import android.content.Context;
import android.hardware.SensorManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FuzzyFitnessAdvisor {
    private UserGoalsFuzzySets userGoalsFuzzySets;
    private BmiFuzzySet bmiFuzzySet;
    private Vo2MaxFuzzySet vo2maxFuzzySet;
    private SleepFuzzySet sleepFuzzySet;
    private FitnessLevelFuzzySet fitnessLevelFuzzySet;
    private WorkoutHistoryFuzzySets workoutHistoryFuzzySets;
    private Context context;

    public FuzzyFitnessAdvisor(Context context) {
        this.context = context;
        bmiFuzzySet = new BmiFuzzySet();
        sleepFuzzySet = new SleepFuzzySet();
        fitnessLevelFuzzySet = new FitnessLevelFuzzySet();
        vo2maxFuzzySet = new Vo2MaxFuzzySet();
    }

    public void loadUserData(double bmi, double vo2max, double sleepHours, int fitnessLevel, List<String> workoutHistory, List<String> userGoalsList) {
        // load user data and calculate memberships
        bmiFuzzySet.calculateClassification(bmi);

        vo2maxFuzzySet.calculateClassification(vo2max);

        sleepFuzzySet.calculateMembership(sleepHours);

        fitnessLevelFuzzySet.calculateMembership(fitnessLevel);

        // Initialize UserGoalsFuzzySets and WorkoutHistoryFuzzySets with respective lists
        userGoalsFuzzySets = new UserGoalsFuzzySets(userGoalsList);
        userGoalsFuzzySets.calculateMemberships();

        workoutHistoryFuzzySets = new WorkoutHistoryFuzzySets(workoutHistory);

        // Process the workout history to get counts of each workout intensity
        // workoutHistoryFuzzySets.calculateMembership();

    }

    public WorkoutRecommendation recommendWorkout() {

        String intensity;
        String duration;

        // Rule for fitness_level == 'Beginner'
        if (this.fitnessLevelFuzzySet.getMainFitnessLevel().equals("Beginner")) {
            if (this.bmiFuzzySet.getBmiClassification().equals("Underweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else {
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", duration, intensity);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else {
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Rest", "", "");
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        return new WorkoutRecommendation("Consult with a professional", "", "");
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        return new WorkoutRecommendation("Consult with a professional", "", "");
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Normal")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", duration, intensity);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Rest", "", "");
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Overweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Rest", "", "");
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Obese")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    intensity = "low";
                    duration = "short";
                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate") ||
                        this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Rest", "", "");
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    }
                }
            }
        }


        if (this.fitnessLevelFuzzySet.getMainFitnessLevel().equals("Intermediate")) {
            if (this.bmiFuzzySet.getBmiClassification().equals("Underweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {

                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) ? new WorkoutRecommendation("Tempo Run", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        return new WorkoutRecommendation("Consult with a professional", "", "");
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    }
                }

                else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {

                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        return new WorkoutRecommendation("Consult with a professional", "", "");
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Normal")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "long";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) ? new WorkoutRecommendation("Tempo Run", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) ? new WorkoutRecommendation("Tempo Run", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) ? new WorkoutRecommendation("Easy Run", intensity, duration) : new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "high";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "long";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) ? new WorkoutRecommendation("Fartlek Training", intensity, duration) : new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) ? new WorkoutRecommendation("Interval Training", intensity, duration) : new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) ? new WorkoutRecommendation("Long Run", intensity, duration) : new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run") || !workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) ? new WorkoutRecommendation("Easy Run", intensity, duration) : new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Overweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                }
            }

            if (this.bmiFuzzySet.getBmiClassification().equals("Obese")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {

                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                }
            }
        }
        if (this.fitnessLevelFuzzySet.getMainFitnessLevel().equals("Advanced")) {
            if (this.bmiFuzzySet.getBmiClassification().equals("Underweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "short";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    }
                }
            } if (this.bmiFuzzySet.getBmiClassification().equals("Normal")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "moderate";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        }
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "short";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    }

                }
            } if (this.bmiFuzzySet.getBmiClassification().equals("Overweight")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "short";
                    } else { // Excellent
                        intensity = "moderate";
                        duration = "long";
                    }
                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Tempo Run")) {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    }
            } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "long";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "high";
                        duration = "short";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }
                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Tempo Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    }
                }
            } if (this.bmiFuzzySet.getBmiClassification().equals("Obese")) {
                if (this.sleepFuzzySet.getSleepClassification().equals("Inadequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor") || this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "short";
                    } else { // Good or Excellent
                        intensity = "moderate";
                        duration = "short";
                    }

                    if (!workoutHistoryFuzzySets.getLastTwoWorkouts().contains("Rest")) {
                        return new WorkoutRecommendation("Rest", "", "");
                    } else {
                        return new WorkoutRecommendation("Easy Run", intensity, duration);
                    }
                } else if (this.sleepFuzzySet.getSleepClassification().equals("Adequate")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "short";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Easy Run")) {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Recovery Run")) {
                            return new WorkoutRecommendation("Recovery Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    }

                } else if (this.sleepFuzzySet.getSleepClassification().equals("Excellent")) {
                    if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Poor")) {
                        intensity = "low";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Fair")) {
                        intensity = "moderate";
                        duration = "moderate";
                    } else if (this.vo2maxFuzzySet.getVo2MaxClassification().equals("Good")) {
                        intensity = "moderate";
                        duration = "long";
                    } else { // Excellent
                        intensity = "high";
                        duration = "long";
                    }

                    if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Overall Fitness")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Increase Running Speed")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Easy Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Weight Loss")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Fartlek Training")) {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        }
                    } else if (this.userGoalsFuzzySets.getMainGoal().equals("Improve Running Endurance")) {
                        if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Long Run")) {
                            return new WorkoutRecommendation("Long Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Progression Run")) {
                            return new WorkoutRecommendation("Progression Run", intensity, duration);
                        } else if (!workoutHistoryFuzzySets.getLastThreeWorkouts().contains("Interval Training")) {
                            return new WorkoutRecommendation("Interval Training", intensity, duration);
                        } else {
                            return new WorkoutRecommendation("Fartlek Training", intensity, duration);
                        }
                    }
                }
        }
        // ... other rules...

        // If no rules are satisfied, return a default recommendation or null
        // return new WorkoutRecommendation(defaultParameters);
        }
        return null;
    }
}

