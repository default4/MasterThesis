package com.example.masterrunning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.masterrunning.presentation.CheckTraining;

public class TrainingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CheckTraining.checkAndUpdateTraining(context);
    }
}