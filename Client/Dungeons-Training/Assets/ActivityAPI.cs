using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class ActivityAPI
{
    public static async void startActivity(string activityType) {
        await API.put<ActivityRequest, string>(Global.baseUrl + "activity", new ActivityRequest{ activityType = activityType}, new Dictionary<string, string>()); 
    }

    public static async void stopActivity(string activityType) {
        await API.delete<string>(Global.baseUrl + "activity", new Dictionary<string, string>());
    }
}

public class ActivityRequest {
    public string activityType;
}