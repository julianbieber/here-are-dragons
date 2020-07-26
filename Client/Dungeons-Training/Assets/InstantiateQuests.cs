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
using UnityEngine.SceneManagement;

[Serializable]
public class InstantiateQuests : MonoBehaviour
{
    public GameObject quest;
    public float distanceInMeter;
    private float nextUpdate;
    private List<DAOQuest> newList = new List<DAOQuest>();
    private List<long> loI = new List<long>();

    private List<Hilfsobjetct> loL = new List<Hilfsobjetct>();



    void Start()
    {
    }

	void Awake()
	{
		nextUpdate = 5f+Global.TimeOfLogin.value;
	}
    async void Update()
    {
        var map = LocationProviderFactory.Instance.mapManager;
        if (Time.time >= nextUpdate )
        {
            nextUpdate = nextUpdate + 10f;
            var I = await QuestAPI.getListOfQuestsNearby(distanceInMeter);
            newList = I.quests;
            List<long> loIn = new List<long>();
            foreach (DAOQuest q in newList)
            {
                loIn.Add(q.questID);
                if (loI.Contains(q.questID))
                {
                    var coord = map.GeoToWorldPosition(new Vector2d(q.latitude, q.longitude));
                    coord.y = 0.5f;
                    foreach (Hilfsobjetct h in loL.ToList())
                    {
                        if (h.id.Equals(q.questID))
                        {
                            h.go.transform.position = coord;
                        }
                    }
                }
                if (!loI.Contains(q.questID) && Time.time >= 2)
                {
                    var coord = map.GeoToWorldPosition(new Vector2d(q.latitude, q.longitude));
                    var l = Instantiate(quest, new Vector3(coord.x, 0.5f, coord.z), Quaternion.identity);
                    string n = q.questID.ToString();
                    l.name = n;
                    l.transform.Rotate(90.0f, 0.0f, 0.0f, Space.Self);
                    loI.Add(q.questID);
                    loL.Add(new Hilfsobjetct(q.questID, l));
                }
            }
            foreach (Hilfsobjetct h in loL.ToList())
            {
                if (!loIn.Contains(h.id))
                {
                    Destroy(h.go);
                    loI.Remove(h.id);
                    loL.Remove(h);
                }
            }
        }
    }
}

public class Hilfsobjetct
{
    public long id;
    public GameObject go;
    public Hilfsobjetct(long i, GameObject go1)
    {
        id = i;
        go = go1;
    }
}
