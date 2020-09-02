using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Text;
using System;

public class Activity : MonoBehaviour
{
    public Button self;
    public List<Button> others;
    public Text countDownText;
    public string activity;
    private bool isRecording = false;

    private float countdownStop = -1;
    private float nextFrame = -1;
    private StreamWriter output;
    private bool vibrated = true;

    // Start is called before the first frame update
    void Start()
    {
        Guid uuid = Guid.NewGuid();
        self.GetComponent<Image>().color = Color.green;
        output = new StreamWriter(Application.persistentDataPath + "/"+ activity + "_" + uuid.ToString() +".csv", true);
    }

    // Update is called once per frame
    void Update()
    {
        if (Time.time < countdownStop) {
            countDownText.text = (countdownStop - Time.time).ToString(); 
        } else {
            if (Time.time >= nextFrame && nextFrame > -1) {
                if (!vibrated) {
                    Handheld.Vibrate();
                    vibrated = true;
                }
                Debug.Log("write line " + activity);
                nextFrame += 0.1f;
                StringBuilder line = new StringBuilder();
                line.Append(activity);
                line.Append(",");

                line.Append(Input.gyro.userAcceleration.x);
                line.Append(",");
                line.Append(Input.gyro.userAcceleration.y);
                line.Append(",");
                line.Append(Input.gyro.userAcceleration.z);
                line.Append(",");

                line.Append(Input.gyro.attitude.x);
                line.Append(",");
                line.Append(Input.gyro.attitude.y);
                line.Append(",");
                line.Append(Input.gyro.attitude.z);
                line.Append(",");
                line.Append(Input.gyro.attitude.w);
                line.Append(",");
                
                line.Append(Input.acceleration.x);
                line.Append(",");
                line.Append(Input.acceleration.y);
                line.Append(",");
                line.Append(Input.acceleration.z);
                line.Append(",");
                line.Append(Time.time);

                output.WriteLine(line.ToString());
                output.Flush();
            }
        }
    }

    public void toggle()
    {
        if (!isRecording) {
            countdownStop = Time.time + 3;
            nextFrame = countdownStop + 1;
            foreach (var other in others)
            {
                other.gameObject.SetActive(false);    
            }
            
            isRecording = true;
            self.GetComponent<Image>().color = Color.red;
            vibrated = false;
        } else {
            countdownStop = -1;
            nextFrame = countdownStop;
            isRecording = false;
            self.GetComponent<Image>().color = Color.green;
            foreach (var other in others)
            {
                other.gameObject.SetActive(true);    
            }
        }
        
    }


}
