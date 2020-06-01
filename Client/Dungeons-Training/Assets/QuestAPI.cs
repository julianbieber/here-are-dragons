using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class QuestAPI : MonoBehaviour
{
    async public static Task<QuestsResponse> getListOfQuestsNearby(float distance) {
	var que = new Dictionary<string, string>();
	que.Add("distance",distance.ToString());
       var response = await API.get<QuestsResponse>(Global.baseUrl + "getListOfQuests", que);
       if (response.isSome) {
	return response.value;      
 } else {
           Debug.Log("No Quests available");
           return new QuestsResponse();
       }
}

}
[Serializable]
public class DAOQuest {
    public int questID;
    public float longitude;
    public float latitude;
}
[Serializable]
public class QuestsResponse{
   public List<DAOQuest> quests;
}
