package com.example.masterrunning.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TrainingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CheckTraining.checkAndUpdateTraining(context);
    }
}
