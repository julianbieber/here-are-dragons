using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.UIElements;

public class showPanelOnButtonClick : MonoBehaviour
{
    public GameObject panel;

    public Text text;

    private int timeOfText;

    async public void showPanel(GameObject p)
    {
        var activeQuestInGroup = await QuestAPI.getActiveQuestInGroup();

        if(!activeQuestInGroup.activ&& p != null){

            p.SetActive(true);

        }
        if(activeQuestInGroup.activ){
            timeOfText = 2000;
            text.text = "Gruppe hat bereits einen Queest ausgewählt";
        }
    
    }

    void Update(){
        if(timeOfText>1){
            timeOfText--;
        }
        if(timeOfText == 1){
            text.text ="";
            timeOfText = 0;
        }
    }
}
