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
    async public static void postUnactivateQuest(long questID)
    {
        var que = new Dictionary<string, string>();
        que.Add("questID", questID.ToString());
        await API.post<string, string>(Global.baseUrl + "unactivateQuest", "", que);
    }

    async public static void postActiveQuest(long questID)
    {
        var que = new Dictionary<string, string>();
        que.Add("questID", questID.ToString());
        await API.post<string, string>(Global.baseUrl + "activateQuest", "", que);
    }

    async public static Task<nextPosition> getNextQuestPosition()
    {
        var que = new Dictionary<string, string>();
        var response = await API.get<nextPosition>(Global.baseUrl + "nextQuestPosition",que);
        if(response.isSome) return response.value;
        else {
            Debug.Log("Quest abgeschlossen");
            return new nextPosition { lanlot = new float[0] };
        }
    }
}
[Serializable]
public class DAOQuest
{
    public long questID;
    public float longitude;
    public float latitude;
    public float priority;
    public String tag;
    public Boolean erledigt=false;

}
[Serializable]
public class QuestsResponse
{
    public List<DAOQuest> quests;
}
[Serializable]
public class nextPosition
{
    public float[] lanlot;
}
