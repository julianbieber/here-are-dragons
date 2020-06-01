using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine;
using Mapbox.Unity.Utilities;
using Mapbox.Utils;
using Mapbox.Unity.Location;
using System.Linq;

[Serializable] 
public class InstantiateQuests : MonoBehaviour
{
    public GameObject quest;
    public float distanceInMeter;
    private int nextUpdate=1;
    private List<DAOQuest> newList=new List<DAOQuest>();
    private List<int> loI=new List<int>();
    
    private List<Hilfsobjetct> loL=new List<Hilfsobjetct>();

    void Start()
    { 	
    }
    
    async void Update()
    { 
	var map = LocationProviderFactory.Instance.mapManager;
	if(Time.time>=nextUpdate)
        {	
            nextUpdate=Mathf.FloorToInt(Time.time)+10;	
	    var I = await QuestAPI.getListOfQuestsNearby(distanceInMeter);
	    newList =I.quests;
	    List<int> loIn=new List<int>();
	    foreach(DAOQuest q in newList){
		loIn.Add(q.questID);
		if(!loI.Contains(q.questID)&&Time.time>=3){
			var coord = map.GeoToWorldPosition(new Vector2d(q.longitude, q.latitude));
	 		var l = Instantiate(quest, new Vector3(coord.x, coord.y, 0), Quaternion.identity);	
			loI.Add(q.questID);
			
			loL.Add(new Hilfsobjetct(q.questID,l));
		}
	    }
	    foreach(Hilfsobjetct h in loL.ToList()){
		if(!loIn.Contains(h.id)){
			Destroy(h.go);
			loI.Remove(h.id);
			loL.Remove(h);
		}
	    }
     	}
    }
}

public class Hilfsobjetct{
	public int id;
	public GameObject go;
	public Hilfsobjetct(int i,GameObject go1){
		id=i;
		go=go1;
	}
}
