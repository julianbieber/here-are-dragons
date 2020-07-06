using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Text;
using System;

public class Skillbar : MonoBehaviour
{
    public List<Button> skillButtons;
    public DungeonController dm;
    public Character character;
    public Text tooltipBox;
    public Image tooltipBackground;
    
    private int nextUpdate = 0;
    // Start is called before the first frame update
    void Start()
    {
        tooltipBackground.enabled = false;
        tooltipBox.enabled = false;
    }

    // Update is called once per frame
    async void Update()
    {
        if (character.player.isSome && character.player.value.skillBar.selected.Count <= skillButtons.Count) {
            for (int i = 0; i < skillButtons.Count; ++i) {
                if (i < character.player.value.skillBar.selected.Count) {
                    var skill = character.player.value.skillBar.selected[i];
                    var skillButton = skillButtons[i];
                    skillButton.GetComponentInChildren<Text>().text = skill.name;
                } 
            }
        }
        
    }

    public void cast(int skillId) {
        if (character.player.isSome && character.player.value.skillBar.selected.Count > skillId) {
            var skill = character.player.value.skillBar.selected[skillId];
        
            displayTooltip(skill);
            dm.makeTargettableForPattern(skill);
        }
    }

    public void show(int skillId) {
        if (character.player.isSome && character.player.value.skillBar.selected.Count > skillId) {
            var skill = character.player.value.skillBar.selected[skillId];
        
            displayTooltip(skill);
        }
    }

    

    private void displayTooltip(Skill skill) {
        tooltipBox.enabled = true;
        tooltipBackground.enabled = true;
        
        var tooltipText = new StringBuilder();
        tooltipText.Append("Name: ");
        tooltipText.Append(skill.name);
        tooltipText.Append("\n");

        tooltipText.Append("valid targets: ");
        tooltipText.Append(skill.targetPattern);
        tooltipText.Append("\n");

        tooltipText.Append("effected fields: ");
        tooltipText.Append(skill.effectPattern);
        tooltipText.Append("\n");

        tooltipText.Append("damage: ");
        tooltipText.Append(skill.damage);
        tooltipText.Append("\n");

        tooltipText.Append("ap cost: ");
        tooltipText.Append(skill.apCost);
        tooltipText.Append("\n");

        if (skill.status.burning > 0) {
            tooltipText.Append("burning: ");
            tooltipText.Append(skill.status.burning);
            tooltipText.Append("\n");
        }

        if (skill.moves) {
            tooltipText.Append("moves to target offset by: ");
            tooltipText.Append(skill.movementOffset);
            tooltipText.Append("\n");
        }
        
        tooltipBox.text = tooltipText.ToString();
    }
}


[Serializable]
public class Skill {
    public int id;
    public string name;
    public string targetPattern;
    public string effectPattern; 
    public int apCost;

    public int damage;

    public Status status;

    public bool moves;
    public int movementOffset;

}