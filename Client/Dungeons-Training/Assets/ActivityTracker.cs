using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using UnityEngine.UI;
using System;

public class ActivityTracker : MonoBehaviour
{
   
    private AndroidJavaClass pluginClass;
    public Text debug;

    // Start is called before the first frame update
    void Start()
    {   
        try {
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            AndroidJavaObject context = activity.Call<AndroidJavaObject>("getApplicationContext");
            pluginClass = new AndroidJavaClass("com.example.activitytracking.Tracker");
            
            pluginClass.CallStatic("initialize", context, activity, Global.baseUrl, Global.userId.value.ToString(), Global.token.value);
        } catch (Exception e) {
            debug.text += e.ToString();
        }
    }

    // Update is called once per frame
    void Update()
    {
        debug.text = pluginClass.CallStatic<string>("getLog");
    }
    
}
