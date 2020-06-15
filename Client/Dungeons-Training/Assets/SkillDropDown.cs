using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using static UnityEngine.UI.Dropdown;

public class SkillDropDown : MonoBehaviour
{

    public Character character;

    private Dropdown dropdown;
    void Start()
    {
        dropdown = GetComponent<Dropdown>();
    }

    // Update is called once per frame
    void Update()
    {
        if (character.player.isSome) {
            dropdown.ClearOptions();
            List<OptionData> options = new List<OptionData>(); 
            options.Add(new OptionData());
            foreach (var skill in character.player.value.skillBar.unlocked) {
                options.Add(new OptionData(skill.id + "|" + skill.name));
            }
            dropdown.AddOptions(options);
        } 
    }

    public void selectSkill(int i) {
        var option = character.player.value.skillBar.unlocked[i - 1];
        CharacterAPI.selectSkill(option.id);
    }
}
