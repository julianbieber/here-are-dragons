using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using Mapbox.Unity.Utilities;
using Mapbox.Utils;
using Mapbox.Unity.Location;

public class GroupMemberController : MonoBehaviour
{
    public GameObject memberPrefab;

    public List<Position> members;

    private Dictionary<int, GameObject> memberObjects; 
    private int nextUpdate=1;
    // Start is called before the first frame update
    void Start()
    {
        members = new List<Position>();
        memberObjects = new Dictionary<int, GameObject>();
    }

    // Update is called once per frame
    async void Update()
    {
        if(Time.time>=nextUpdate)
        {
            nextUpdate=Mathf.FloorToInt(Time.time)+1;
            var map = LocationProviderFactory.Instance.mapManager;
            members = await GroupAPI.getGroup();
            var currentMemberIds = new List<int>();
            foreach (var position in members)
            {
                if (position.userID != Global.userId.value) {
                    if (!memberObjects.ContainsKey(position.userID)) {
                        var memberObject = Instantiate(memberPrefab, new Vector3(0, 0, 0), Quaternion.identity);
                        memberObjects.Add(position.userID, memberObject);
                    }
                    var worldPosition = map.GeoToWorldPosition(new Vector2d(position.longitude, position.latitude));
                    memberObjects[position.userID].transform.localPosition = worldPosition;
                    currentMemberIds.Add(position.userID);
                }
                
            }

            var memberObjectKeys = new int[(memberObjects.Keys.Count)];
            memberObjects.Keys.CopyTo(memberObjectKeys, 0);

            foreach (var oldUserId in memberObjectKeys)
            {
                if (!currentMemberIds.Contains(oldUserId)) {
                    memberObjects[oldUserId].Destroy();
                    memberObjects.Remove(oldUserId);
                }
            }
        }
    }
}
