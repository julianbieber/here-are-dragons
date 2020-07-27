using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ErmittleOnQuesterfullt : MonoBehaviour
{
    private int nextUpdate;
    public GameObject Player;

    public Text text;

    private int i = 0;
    void Start()
    {
        nextUpdate = 1;
    }
    void Update()
    {
        if(i==1){
            i=0;
            text.text="";
        }
        if(i>1) i =i-1;
        if (Time.time > nextUpdate * 10)
        {
            Quest q = new Quest(Global.ausgewahlterQuest, Player);
            bool Questerfullt = q.istPlayerAnPositionVonAusgewahltemQuest();
            nextUpdate++;
            if (Questerfullt && q.ausgewählterQuest.isSome&&!Global.ausgewahlterQuest.value.erledigt)
            {
                text.text = "Quest abgeschlossen";
                i = 2000;
                QuestAPI.postUnactivateQuest(Global.ausgewahlterQuest.value.questID);
                Global.ausgewahlterQuest.value.erledigt = true;
            }
        }
    }
}
