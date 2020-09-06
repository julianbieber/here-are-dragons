using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class DifficultyAPI : MonoBehaviour
{

    async public static void postDifficulty(int difficulty, bool group)
    {
        var que = new Dictionary<string, string>();
        que.Add("difficulty",  difficulty.ToString());
        que.Add("group",group.ToString());
        await API.post<string, string>(Global.baseUrl + "difficulty", "", que);
    }

    async public static Task<DifficultyResponse> getAvailableDungeons(){
        var response = await API.get<DifficultyResponse>(Global.baseUrl + "difficulty", new Dictionary<string, string>());
        if (response.isSome) {
            return response.value;
        } else {
            Debug.Log("No Quests completed");
            return new DifficultyResponse();
        }
    }
}
[Serializable]
public class DifficultyResponse {
    public List<ExtendedDifficultyRow> difficulties;
}
[Serializable]
public class ExtendedDifficultyRow {
     
    public int id;
    public int difficulty;
    public List<String> members;
}

