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
                var activeQuestInGroup = await QuestAPI.getActiveQuestInGroup();
                if (!activeQuestInGroup.activ){
                    var questid = (long) Global.ausgewahlterQuest.value.questID;
                    QuestAPI.postActiveQuest(questid,Global.difficulty.value);
                }else{
                    Global.ausgewahlterQuest = Option<DAOQuest>.None;
                    QuestAPI.postUnactivateQuest1();
                }
            }else{
                QuestAPI.postUnactivateQuest1();
            }
        }
    }
}
