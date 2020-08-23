using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public class ActivityAPI
{
    public static async void startActivity(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.put<string, string>(Global.baseUrl + "activity", "", query); 
    }

    public static void stopActivity(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        API.delete<string>(Global.baseUrl + "activity", query);
    }

    public static async void startRelayRace(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.put<string, string>(Global.baseUrl + "relay", "", query); 
    }

    
    public static void stopRelayRace(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        API.delete<string>(Global.baseUrl + "relay", query);
    }
}