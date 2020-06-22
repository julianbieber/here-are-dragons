package com.example.activitytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.DetectedActivity;

public class TransitionsReceiver extends BroadcastReceiver {

    public static String receiverLogMessage;

    public TransitionsReceiver() {
        if (receiverLogMessage == null) {
            receiverLogMessage = "abc";
        } else {
            receiverLogMessage += "abc";
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            receiverLogMessage += "receive";

            // TODO: Extract activity transition information from listener.
            if (ActivityRecognitionResult.hasResult(intent)) {

                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                result.getMostProbableActivity().getType();
                receiverLogMessage += toActivityString(result.getMostProbableActivity().getType()) + "\n";
            } else {
                receiverLogMessage += "ActivityTransitionResult does not have a result";
            }
        } catch (Throwable e) {
            receiverLogMessage += e.getMessage();
        }
    }

    private String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }
    private String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

}
