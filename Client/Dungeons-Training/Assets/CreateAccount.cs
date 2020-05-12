using UnityEngine;
using UnityEngine.UI;


public class CreateAccount : MonoBehaviour
{
    public Text userNameField;
    public Text passwordField;

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

    public async void Create() {
        var userIdOption = await AccountAPI.createAccount(userNameField.text, passwordField.text);

        if (!userIdOption.isSome) {
            feedbackField.text = "Failed to create account";
        } else {
            feedbackField.text = "Created account with id: " + userIdOption.value.ToString();
        }
    } 

}

