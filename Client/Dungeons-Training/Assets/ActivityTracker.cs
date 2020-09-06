using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using UnityEngine.UI;
using System;

public class ActivityTracker : MonoBehaviour
{
   
    private AndroidJavaClass pluginClass;

    // Start is called before the first frame update
    void Start()
    {   
        try {
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            AndroidJavaObject context = activity.Call<AndroidJavaObject>("getApplicationContext");
            pluginClass = new AndroidJavaClass("com.example.activitytracking.Tracker");
            pluginClass.CallStatic("initialize", context, activity, Global.baseUrl, Global.userId.value, Global.token.value);
        } catch (Exception e) {
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (pluginClass != null) {
            try{
            } catch (Exception e) {
            } 
        } 
    }
    
}
