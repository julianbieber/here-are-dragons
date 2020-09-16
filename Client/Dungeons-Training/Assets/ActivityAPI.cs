using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Linq;

public class ActivityAPI
{
    public static async void startActivity(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.put<string, string>(Global.baseUrl + "activity", "", query); 
    }

    public static async void stopActivity(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.delete<string>(Global.baseUrl + "activity", query);
    }

    public static async void startRelayRace(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.put<string, string>(Global.baseUrl + "relay", "", query); 
    }

    
    public static async void stopRelayRace(string activityType) {
        var query = new Dictionary<string, string>();
        query.Add("type", activityType);
        await API.delete<string>(Global.baseUrl + "relay", query);
    }

    public static async void recordCalisthenics(float[] vector) {
        var query = new Dictionary<string, string>();
        var data = new CalisthenicsPutBody {
            vector = vector.ToList()
        };
        var response = await API.put<CalisthenicsPutBody, string>(Global.baseUrl + "calisthenics", data, query);
    }
}

[Serializable]
public class CalisthenicsPutBody {
    public List<float> vector;
}