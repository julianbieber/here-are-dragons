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
}

