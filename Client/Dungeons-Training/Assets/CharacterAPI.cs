using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class CharacterAPI
{
    public static async Task<Option<PlayerCharacter>> getCharacter() {
        var response = await API.get<PlayerCharacter>(Global.baseUrl + "character", new Dictionary<string, string>());
        if (response.isSome) {
            return response;
        } else {
            Debug.Log("Failed to retrieve character");
            return Option<PlayerCharacter>.None;
        }
    }

    public static async void selectSkill(int id) {
        var query = new Dictionary<string, string>();
        query.Add("skill", id.ToString());
        var response = await API.post<string, PlayerSkillBar>(Global.baseUrl + "character/select", "", query);
        if (!response.isSome) {
            Debug.Log("Failed to select skill");
        }
    }
}

[Serializable]
public class PlayerCharacter {
    public long rangerExperience;
    public long sorcererExperience;
    public long warriorExperience;
    public PlayerSkillBar skillBar;
}

[Serializable]
public class PlayerSkillBar {
    public List<Skill> selected;
    public List<Skill> unlocked;
}

