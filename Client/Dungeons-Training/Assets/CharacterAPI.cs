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
        var response = await API.post<string, PlayerSkillBar>(Global.baseUrl + "character/skills/select", "", query);
        if (!response.isSome) {
            Debug.Log("Failed to select skill");
        }
    }

    public static async void unselectSkill(int id) {
        var query = new Dictionary<string, string>();
        query.Add("skill", id.ToString());
        var response = await API.delete<PlayerSkillBar>(Global.baseUrl + "character/skills/select", query);
        if (!response.isSome) {
            Debug.Log("Failed to select skill");
        }
    }

    public static async void unlockAttribute(Attributes diff) {
        var response = await API.post<Attributes, string>(Global.baseUrl + "character/attributes/unlock", diff, new Dictionary<string, string>());
        if (!response.isSome) {
            Debug.Log("Failed to unlock attributes");
        }
    }

    public static async void selectAttributes(Attributes diff) {
        var response = await API.post<Attributes, string>(Global.baseUrl + "character/attributes/select", diff, new Dictionary<string, string>());
        if (!response.isSome) {
            Debug.Log("Failed to select attributes");
        }
    }

    public static async void levlelUp() {
        var response = await API.post<string, string>(Global.baseUrl + "character/levelUp", "", new Dictionary<string, string>());
        if (!response.isSome) {
            Debug.Log("Failed to select attributes");
        }
    }
}

[Serializable]
public class PlayerCharacter {
    public long rangerExperience;
    public long sorcererExperience;
    public long warriorExperience;
    public PlayerSkillBar skillBar;
    public Attributes unlockedAttributes;
    public Attributes selectedAttributes;
    public bool canLevelUp;
    public bool canUnlockWarrior;
    public bool canUnlockSorcerer;
    public bool canUnlockRanger;
    public int maxSelectableAttributes;
}

[Serializable]
public class PlayerSkillBar {
    public List<Skill> selected;
    public List<Skill> unlocked;
}

[Serializable]
public class Attributes {
    public int strength;
    public int constitution;
    public int spellPower;
    public int willPower;
    public int dexterity;
    public int evasion;

    public int sum() {
        return strength + constitution + spellPower + willPower + dexterity + evasion;
    }
}

