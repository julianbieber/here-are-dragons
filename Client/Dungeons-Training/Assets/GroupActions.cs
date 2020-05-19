using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GroupActions : MonoBehaviour
{
    public InputField otherUserField;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void join() {
        GroupAPI.joinGroup(otherUserField.text);
    }

    public void leave() {
        GroupAPI.leaveGroup();
    }
}
