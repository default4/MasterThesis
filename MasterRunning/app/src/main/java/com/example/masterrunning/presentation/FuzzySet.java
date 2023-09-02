package com.example.masterrunning.presentation;

import java.util.HashMap;
import java.util.Map;

public class FuzzySet {
    private String name;
    private Map<Integer, Double> membershipFunction;

    public FuzzySet(String name) {
        this.name = name;
        this.membershipFunction = new HashMap<>();
    }

    public void addPoint(int x, double membershipDegree) {
        membershipFunction.put(x, membershipDegree);
    }

    public double getMembershipDegree(int x) {
        return membershipFunction.getOrDefault(x, 0.0);
    }

    public String getName() {
        return this.name;
    }
}
