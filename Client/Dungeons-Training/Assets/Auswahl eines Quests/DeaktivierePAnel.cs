using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.UIElements;
public class DeaktivierePAnel : MonoBehaviour
{
    public GameObject panel;
    async public void disablePanel(GameObject p)
    {
        if (p != null)
        {
            p.SetActive(false);

            // Sende Requuest an Server, damit der Server den aktiven Quest kennt, dazu den global quest senden
            if(Global.ausgewahlterQuest.isSome){
                
                var a = (long) Global.ausgewahlterQuest.value.questID;
                QuestAPI.postActiveQuest(a);
            }
        }
    }
}
