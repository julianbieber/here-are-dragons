using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.UIElements;
public class DeaktivierePAnel : MonoBehaviour
{
    async public void disablePanel(GameObject panel)
    {
        if (panel != null)
        {
            panel.SetActive(false);
            // Sende Request an Server, damit der Server den aktiven Quest kennt, dazu den global quest senden
            if(Global.ausgewahlterQuest.isSome){
                var questid = (long) Global.ausgewahlterQuest.value.questID;
                QuestAPI.postActiveQuest(questid);
            }
        }
    }
}
