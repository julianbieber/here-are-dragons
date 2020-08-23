using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;
using System.Text;

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

    public static void unselectSkill(int id) {
        var query = new Dictionary<string, string>();
        query.Add("skill", id.ToString());
        API.delete<PlayerSkillBar>(Global.baseUrl + "character/skills/select", query);
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

    public string printable() {
        StringBuilder b = new StringBuilder();
        b.Append("Strength: ");
        b.Append(strength);
        b.Append("\n");
        b.Append("con: ");
        b.Append(constitution);
        b.Append("\n");
        b.Append("spellpower: ");
        b.Append(spellPower);
        b.Append("\n");
        b.Append("will: ");
        b.Append(willPower);
        b.Append("\n");
        b.Append("dex: ");
        b.Append(dexterity);
        b.Append("\n");
        b.Append("eva: ");
        b.Append(evasion);
        b.Append("\n");
        return b.ToString();
    }
}

