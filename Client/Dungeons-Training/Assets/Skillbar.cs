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
    public Text tooltipBox;
    public Image tooltipBackground;
    
    private int nextUpdate = 0;
    private List<Skill> skills;
    private int ap;
    private bool myTurn;
    // Start is called before the first frame update
    void Start()
    {
        tooltipBackground.enabled = false;
        tooltipBox.enabled = false;
    }

    // Update is called once per frame
    async void Update()
    {
        
    }

    public void updateCharacter(List<Skill> skills, int ap, bool myTurn) {
        this.skills = skills;
        this.ap = ap;
        this.myTurn = myTurn;
        for (int i = 0; i < skillButtons.Count; ++i) {
            if (i < skills.Count) {
                var skill = skills[i];
                var skillButton = skillButtons[i];
                skillButton.GetComponentInChildren<Text>().text = skill.name;
                if (skill.remainingCoolDown > 0 || skill.apCost > ap || !myTurn) {
                    skillButton.GetComponent<Image>().color = Color.red;
                } else {
                    skillButton.GetComponent<Image>().color = Color.green;
                }
            } 
        }
    }

    public void cast(int skillId) {
        if (skills != null && skills.Count > skillId) {
            var skill = skills[skillId];
            displayTooltip(skill);
            if (skill.remainingCoolDown == 0 && skill.apCost <= ap && myTurn) {
                dm.makeTargettableForPattern(skill);    
            }
        }
    }

    public void show(int skillId) {
        if (skills != null && skills.Count > skillId) {
            var skill = skills[skillId];
        
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
    public float strengthScaling;
    public float spellPowerScaling;
    public float dexterityScaling;
    public Status status;
    public bool moves;
    public int movementOffset;
    public int coolDown;
    public int remainingCoolDown;
    public Attributes attributesOffset;
    public int attributesOffsetDuration;

}