using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Mapbox.Utils;
using Mapbox.Unity.Location;
using System.Linq;
using Mapbox.Unity.Utilities;
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
    async void Update()
    {
        if(i==1){
            i=0;
            text.text=" ";
        }
        if(i>1) i =i-1;
        if (Time.time > nextUpdate * 10)
        {
            Quest q = new Quest(Global.ausgewahlterQuest, Player);
            bool Questerfullt = q.istPlayerAnPositionVonAusgewahltemQuest();
            nextUpdate++;
            Debug.Log(Global.ausgewahlterQuest.isSome);
            if (Questerfullt && q.ausgewählterQuest.isSome&&!Global.ausgewahlterQuest.value.erledigt)
            {
                var b = await QuestAPI.getNextQuestPosition();
                Debug.Log(b.latlong);
                if(b.latlong==null || !(b.latlong.GetLength(0)).Equals(2) ){
                    text.text = "Quest abgeschlossen ! :)";
                    List<Position> a =  await GroupAPI.getGroup();
                    DifficultyAPI.postDifficulty(Global.difficulty.value, (a.Count>0));
                    i = 2000;
                    QuestAPI.postUnactivateQuest(Global.ausgewahlterQuest.value.questID);
                    Global.ausgewahlterQuest.value.erledigt = true;
                    Global.erledigt = Option<bool>.Some(true);
                    GameObject Quest = GameObject.Find(Global.ausgewahlterQuest.value.questID.ToString());
                    Destroy(Quest);
                    Global.ausgewahlterQuest=Option<DAOQuest>.None;
                }else{
                    var map = LocationProviderFactory.Instance.mapManager;
                    Global.ausgewahlterQuest.value.latitude = b.latlong[0];
                    Global.ausgewahlterQuest.value.longitude = b.latlong[1];
                    var coord = map.GeoToWorldPosition(new Vector2d(Global.ausgewahlterQuest.value.latitude, Global.ausgewahlterQuest.value.longitude));
                    GameObject Quest = GameObject.Find(Global.ausgewahlterQuest.value.questID.ToString());
                    Quest.transform.position = coord;
                }
            }
        }
    }
}
