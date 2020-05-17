using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Login : MonoBehaviour
{

    public InputField userNameField;
    public InputField passwordField;

    public Text feedbackField;
    public Button self;

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
            feedbackField.text = "Login Success"; // TODO switch scene
        } else {
            feedbackField.text = "Failed to login";
        }
    }
}
