using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using UnityEngine.UI;
using System;

public class ActivityTracker : MonoBehaviour
{
   

    public Text debug;
    private AndroidJavaClass pluginClass;

    // Start is called before the first frame update
    void Start()
    {   
        try {
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            AndroidJavaObject activity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            AndroidJavaObject context = activity.Call<AndroidJavaObject>("getApplicationContext");
            pluginClass = new AndroidJavaClass("com.example.activitytracking.Tracker");
            pluginClass.CallStatic("initialize", context, activity);
        } catch (Exception e) {
            debug.text = e.Message;
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (pluginClass != null) {
            try{
                debug.text = pluginClass.CallStatic<string>("getLog");
            } catch (Exception e) {
                debug.text += e.Message;
            } 

        } else {
            debug.text = "pluginClass == null";
        }
    }
    
}
