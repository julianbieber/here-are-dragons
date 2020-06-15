using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class QuestAPI : MonoBehaviour
{
    async public static Task<QuestsResponse> getListOfQuestsNearby(float distance)
    {
        var que = new Dictionary<string, string>();
        que.Add("distance", distance.ToString());
        var response = await API.get<QuestsResponse>(Global.baseUrl + "getListOfQuests", que);
        if (response.isSome)
        {
            return response.value;
        }
        else
        {
            Debug.Log("No Quests available");
            return new QuestsResponse { quests = new List<DAOQuest>() };
        }
    }
    async public static void postQuestErledigt(int questID)
    {
        var que = new Dictionary<string, string>();
        que.Add("questID", questID.ToString());
        await API.post<string, string>(Global.baseUrl + "postQuestErledigt", "", que);
    }
}
[Serializable]
public class DAOQuest
{
    public int questID;
    public float longitude;
    public float latitude;
    public Boolean erledigt=false;
}
[Serializable]
public class QuestsResponse
{
    public List<DAOQuest> quests;
}
