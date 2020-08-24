using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Text;

public class TalentDisplay : MonoBehaviour
{
    public Text tooltip;
    public Talent talent;
    public GroupTalent groupTalent;
    public LoadTalentTree loadTalentTree;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void setTalent(Talent talent) {
        this.talent = talent;
        Debug.Log("Set talent");
        transform.GetChild(0).gameObject.GetComponent<Text>().text = talent.name;
    
    }
    public void setTalent(GroupTalent talent) {
        Debug.Log("set group talent");
        this.groupTalent = talent;
        transform.GetChild(0).gameObject.GetComponent<Text>().text = talent.name;
    }

    public async void select(bool executeSelect) {
        if (talent != null) {
            tooltip.text = buildRequirements(talent);
            loadTalentTree.load(talent.activityId);
            if (executeSelect) {
                var _ = await TalentAPI.startUnlocking(talent.id, false);
            }
        } else if (groupTalent != null) {
            tooltip.text = buildRequirements(groupTalent);
            loadTalentTree.load(groupTalent.activityId);
            if (executeSelect) {
                var _ = await TalentAPI.startUnlocking(groupTalent.id, true);
            }
        }
        
    }

    string buildRequirements(Talent talent) {
        var builder = new StringBuilder();
        builder.Append("To unlock ");
        builder.Append(talent.name);
        builder.Append(" you have to:\n");

        if (talent.activityId == 1 || talent.activityId == 2) {
            builder.Append(activityIdToString(talent.activityId));
            if (talent.distance != 0 && talent.speed == 0) {
                builder.Append(" for ");
                builder.Append(talent.distance);
                builder.Append(" meters");
            } else if (talent.speed != 0 && talent.distance == 0) {
                builder.Append("Any distance with an average speed of ");
                builder.Append(talent.speed);
                builder.Append("km/h");
            } else if (talent.time != 0) {
                builder.Append(talent.time);
                builder.Append(" minutes");
            } else if (talent.distance != 0 && talent.speed != 0) {
                builder.Append(" ");
                builder.Append(talent.speed);
                builder.Append("km/h for a distance of ");
                builder.Append(talent.distance);
                builder.Append(" meters.");
            } else if (talent.timeInDay != 0) {
                builder.Append(talent.timeInDay);
                builder.Append(" minutes over the course of 24 hours");
            }
        } else {
            builder.Append("Spend ");
            builder.Append(talent.time);
            builder.Append(" minutes in any training facility");
        }

        return builder.ToString();
    }

    string buildRequirements(GroupTalent talent) {
        var builder = new StringBuilder();
        builder.Append("To unlock ");
        builder.Append(talent.name);
        builder.Append(" your group has to:\n");

        if (talent.activityId == 1 || talent.activityId == 2) {
            builder.Append(activityIdToString(talent.activityId));
            if (talent.distance != 0 && talent.speed == 0) {
                builder.Append(" for ");
                builder.Append(talent.distance);
                builder.Append(" meters");
            } else if (talent.speed != 0 && talent.distance == 0) {
                builder.Append("Any distance with an average speed of ");
                builder.Append(talent.speed);
                builder.Append("km/h");
            } else if (talent.time != 0) {
                builder.Append(talent.time);
                builder.Append(" minutes");
            } else if (talent.distance != 0 && talent.speed != 0) {
                builder.Append(" ");
                builder.Append(talent.speed);
                builder.Append("km/h for a distance of ");
                builder.Append(talent.distance);
                builder.Append(" meters.");
            } 
        } 
        builder.Append("You can complete the challenge in a relay race style.");

        return builder.ToString();
    }

    string activityIdToString(int activityId) {
        if (activityId == 1) {
            return "Run";
        } else if (activityId == 2) {
            return "Cycle";
        } else {
            return "---";
        }
    }
}
