using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using KKUserActivityRecognition;
using System.Threading.Tasks;
using UnityEngine.UI;

public class ActivityTracker : MonoBehaviour
{
    private Option<ActivityType> currentActivity = Option<ActivityType>.None;

    public Text debug;

    // Start is called before the first frame update
    void Start()
    {   
        debug.text = "";
        bool isAvailable = UserActivityRecognition.IsAvailable();

		if (isAvailable) {
			GetComponent<UserActivityListener>()
				.onUserActivityRecognized
				.AddListener(OnActivityRecognized);
            UserActivityRecognition.StartUpdates();
		} else {
            debug.text = "Activity Tracker is unavailable"; 
			Debug.Log("Activity Tracker is unavailable");
		}
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    async void OnActivityRecognized(UserActivityInfo activityInfo) {
        var newType = activityInfo.recognizedType;

        if (!currentActivity.isSome && isKnownActivity(newType)) {
            currentActivity = Option<ActivityType>.Some(newType);
            ActivityAPI.startActivity(toActivityString(newType));
        }

        if (currentActivity.isSome && currentActivity.value != newType && isKnownActivity(newType)) {
            ActivityAPI.stopActivity(toActivityString(currentActivity.value));
            await Task.Delay(10);
            currentActivity = Option<ActivityType>.Some(newType);
            ActivityAPI.startActivity(toActivityString(newType));
        }

        if (currentActivity.isSome && currentActivity.value != newType && !isKnownActivity(newType)) {
            ActivityAPI.stopActivity(toActivityString(currentActivity.value));
            currentActivity = Option<ActivityType>.None;
        }

    }
    
    bool isKnownActivity(ActivityType t) {
        return t == ActivityType.cycling || t == ActivityType.running;
    }

    string toActivityString(ActivityType t) {
        if (t == ActivityType.cycling) {
            return "CYCLING";
        }
        if (t == ActivityType.running) {
            return "RUNNING";
        }
        throw new System.Exception("Unknown activity");
    }

    bool matchesCurrentActivity(ActivityType t) {
        if (currentActivity.isSome) {
            return t == currentActivity.value;
        } else {
            return false;
        }
    }
}
