using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ErmittleOnQuesterfullt : MonoBehaviour
{
    private int nextUpdate;
    public GameObject Player;
    void Start()
    {
        nextUpdate = 1;
    }
    void Update()
    {
        if (Time.time > nextUpdate * 10)
        {

            Quest q = new Quest(Global.ausgewahlterQuest, Player);
            bool Questerfullt = q.istPlayerAnPositionVonAusgewahltemQuest();
            nextUpdate++;
            if (Questerfullt && q.ausgewählterQuest.isSome)
            {
                QuestAPI.postQuestErledigt(q.ausgewählterQuest.value.questID);
                Global.ausgewahlterQuest.value.erledigt = true;
            }
        }
    }
}
