using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;


public class TalentAPI 
{
    public static async Task<Option<TalentResponse>> getTalents() {
        var response = await API.get<TalentResponse>(Global.baseUrl + "talents", new Dictionary<string, string>());
        if (response.isSome) {
            return response;
        } else {
            Debug.Log("Failed to retrieve talents");
            return response;
        }
    }

    public static async Task<Option<Talent>> startUnlocking(int talentId) {
        var query = new Dictionary<string, string>();
        query.Add("id", talentId.ToString());
        var response = await API.get<Talent>(Global.baseUrl + "talents", query);
        if (response.isSome) {
            return response;
        } else {
            Debug.Log("Failed to start unlocking talent");
            return response;
        }
    }
}

[Serializable]
public class TalentResponse {
    public Talent unlocking;
    public List<Talent> unlockOptions;
    public GroupTalent groupUnlocking;
    public List<GroupTalent> groupUnlockOptions;
}

[Serializable]
public class Talent {
    public int id;
    public string name;
    public int skillUnlock;
    public int activityId;
    public int distance;
    public int speed;
    public int time;
    public int timeInDay;
}


[Serializable]
public class GroupTalent {
    public int id;
    public string name;
    public int skillUnlock;
    public int activityId;
    public int distance;
    public int speed;
    public int time;
}