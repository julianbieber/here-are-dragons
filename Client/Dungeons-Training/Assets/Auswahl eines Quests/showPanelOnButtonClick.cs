using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.UIElements;

public class showPanelOnButtonClick : MonoBehaviour
{
    public GameObject panel;
    public void showPanel(GameObject p){
        if(p != null){
            p.SetActive(true);
        }
    }
}
