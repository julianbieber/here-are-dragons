using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

using UnityEngine.SceneManagement;


public class Login : MonoBehaviour
{

    public InputField userNameField;
    public InputField passwordField;

    public Text feedbackField;
    public Button self;

    public float t= Mathf.Infinity;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
       
    }

    public async void login() { 
        var loginResponseOption = await AccountAPI.login(userNameField.text, passwordField.text);
        if (loginResponseOption.isSome) {
            var loginResponse = loginResponseOption.value;
            Global.userId = Option<int>.Some(loginResponse.Item1);
            Global.token = Option<string>.Some(loginResponse.Item2);
            SceneManager.LoadScene("Map");
            Global.TimeOfLogin=Option<float>.Some(Time.time);
        } else {
            feedbackField.text = "Failed to login";
        }
    }
}
