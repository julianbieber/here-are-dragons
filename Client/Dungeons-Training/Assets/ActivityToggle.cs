using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ActivityToggle : MonoBehaviour
{

    public string activity;

    private bool active;

    // Start is called before the first frame update
    void Start()
    {
        active = false;
        GetComponent<Image>().color = Color.gray;
        GetComponentInChildren<Text>().text = "Start relay race (" + activity + ")";
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void toggle() {
        if (!active) {
            ActivityAPI.startRelayRace(activity);
            active = true;
            GetComponent<Image>().color = Color.red;
            GetComponentInChildren<Text>().text = "Stop relay race (" + activity + ")";
        } else {
            ActivityAPI.stopRelayRace(activity);
            GetComponent<Image>().color = Color.gray;
            GetComponentInChildren<Text>().text = "Start relay race (" + activity + ")";
            active = false;
        }
    }


}
