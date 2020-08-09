using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class activity : MonoBehaviour
{

    public Button pullUpsButton;
    public Button pushUpsButton;
    public Button stopButton;
    public Text text;
    public Text countDownText;

    // Start is called before the first frame update
    void Start()
    {
        pullUpsButton.gameObject.SetActive(true);
        pushUpsButton.gameObject.SetActive(true);
        text.gameObject.SetActive(true);
        stopButton.gameObject.SetActive(false);
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void StartActivity(string activity)
    {
        ToggleStartView();
    }
    
    public void StopActivity()
    {
        ToggleStartView();
        countDown();
    }

    public void ToggleStartView()
    {
        bool active = text.gameObject.activeSelf;
        print(active);
        pullUpsButton.gameObject.SetActive(!active);
        pushUpsButton.gameObject.SetActive(!active);
        text.gameObject.SetActive(!active);
        stopButton.gameObject.SetActive(active);
    }

    void countDown()
    {
        int countDown = 3;
        while (countDown>0);
        {
            countDownText.text = countDown.ToString();
            print(countDownText.text);
        }

    }
}
