using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CalisthenicsToggle : MonoBehaviour
{
    public Button self;
    private bool active;
    private List<float> data; 
    private float nextFrame = -1;

    // Start is called before the first frame update
    void Start()
    {
        active = false;
        self.GetComponent<Image>().color = Color.green;
    }

    // Update is called once per frame
    void Update()
    {
        if (active && Time.time >= nextFrame) {
            data.Add(Input.acceleration.x);
            data.Add(Input.acceleration.y);
            data.Add(Input.acceleration.z);
            nextFrame += 0.1f;
            if (data.Count == 90) {
                float[] dataCopy = new float[90];
                data.CopyTo(dataCopy);
                ActivityAPI.recordCalisthenics(dataCopy);
                data = new List<float>();
            }
        }
    }

    public void toggle() {
        if (active) {
            self.GetComponent<Image>().color = Color.red;
        } else {
            self.GetComponent<Image>().color = Color.green;
        }
        active = !active;
        data = new List<float>();
        nextFrame = Time.time;

    }
}


