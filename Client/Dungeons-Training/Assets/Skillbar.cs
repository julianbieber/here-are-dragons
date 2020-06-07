using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Text;

public class Skillbar : MonoBehaviour
{
    public List<Button> skillButtons;
    private List<Option<Skill>> skills = new List<Option<Skill>>();
    public DungeonController dm;
    public Text tooltipBox;
    public Image tooltipBackground;
    // Start is called before the first frame update
    void Start()
    {
        skills.Add(Option<Skill>.Some(new Skill {
            name = "Fireball",
            targetPattern = "11011",
            effectPattern = "111",
            apCost = 1 
        }));
        skills.Add(Option<Skill>.None);
        skills.Add(Option<Skill>.None);
        skills.Add(Option<Skill>.None);
        skills.Add(Option<Skill>.None);
        tooltipBackground.enabled = false;
        tooltipBox.enabled = false;
    }

    // Update is called once per frame
    void Update()
    {
        for (int i = 0; i < skills.Count && i < skillButtons.Count; ++i) {
            var skillO = skills[i];
            var skillButton = skillButtons[i];
            if (skillO.isSome){
                var skill = skillO.value;
                skillButton.GetComponentInChildren<Text>().text = skill.name;
                // Todo replace text with a sprite
            } else {
                skillButton.GetComponentInChildren<Text>().text = (i + 1).ToString();
                skillButton.enabled = false;
            }
            
        }
    }

    public void cast(int skillId) {
        var skillO = skills[skillId];
        if (skillO.isSome) {
            var skill = skillO.value;
            displayTooltip(skill);
            dm.makeTargettableForPattern(skill.targetPattern);
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

        tooltipText.Append("ap cost: ");
        tooltipText.Append(skill.apCost);
        tooltipText.Append("\n");
        
        tooltipBox.text = tooltipText.ToString();
    }
}


public class Skill {
    public string name;
    public string targetPattern;
    public string effectPattern; 
    public int apCost;
}