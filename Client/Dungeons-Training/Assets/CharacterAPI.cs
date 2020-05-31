using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class CharacterAPI
{
    public static async Task<CharacterResponse> getCharacter() {
        var response = await API.get<CharacterResponse>(Global.baseUrl + "character", new Dictionary<string, string>());
        if (response.isSome) {
            return response.value;
        } else {
            Debug.Log("Failed to retrieve character");
            return new CharacterResponse{ rangerExperience = -1, sorcererExperience = -1, warriorExperience = -1};
        }
    }
}

[Serializable]
public class CharacterResponse {
    public long rangerExperience;
    public long sorcererExperience;
    public long warriorExperience;
}


