using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ActivityToggle : MonoBehaviour
{

    public string activity;

    private bool active;

    // Start is called before the first frame update
    void Start()
    {
        active = false;
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void toggle() {
        if (active) {
            ActivityAPI.stopActivity(activity);
            active = false;
        } else {
            ActivityAPI.startActivity(activity);
            active = true;
        }
    }


}
