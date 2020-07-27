using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mapbox.Unity.Location;
using Mapbox.Utils;
using UnityEngine;
using UnityEngine.UI;

public class DistanceToQuest : MonoBehaviour
{
    public GameObject player;

    public GameObject text;

    private int nextUpdate=1;

    // Update is called once per frame
    void Update()
    {
        if(Time.time>=nextUpdate){
            nextUpdate=Mathf.FloorToInt(Time.time)+1;
            if(Global.ausgewahlterQuest.isSome){
                Quest quest = new Quest(Option<DAOQuest>.Some(Global.ausgewahlterQuest.value),player);
                text.GetComponent<Text>().text = Mathd.Floor(quest.getDistanceToPlayer()).ToString();
            }
            else{
                text.GetComponent<Text>().text = "Inf";
            }
        }

    }
}
