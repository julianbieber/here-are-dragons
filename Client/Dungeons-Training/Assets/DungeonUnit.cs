using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class DungeonUnit : MonoBehaviour
{
    public GameObject onFire;
    public GameObject targettable;
    public List<GameObject> empties;
    public List<GameObject> npcs;
    public GameObject player;
    public Unit Self;
    public int index;
    public TextMesh hpText;

    public DungeonController gm;

    // Start is called before the first frame update
    
    private Option<Skill> whenSkillUse = Option<Skill>.None;
    void Start()
    {
        
        setNotOnFire();
        setNotTargettable();
    }

    public void make(Unit unit) {
        if (unit is PlayerUnit) {
            var p = unit as PlayerUnit;
            if (Self == null || !(Self is PlayerUnit)) {
                reset();
                player.SetActive(true);
            }
        } 
        if (unit is NPCUnit) {
            var n = unit as NPCUnit;
            if (Self == null || !(Self is NPCUnit) || (Self as NPCUnit).prefabId != n.prefabId) {
                reset();
                npcs[n.prefabId].SetActive(true);
            }
        }
        if (unit is EmptyUnit) {
            var e = unit as EmptyUnit;
            if (Self == null || !(Self is EmptyUnit) || (Self as EmptyUnit).prefabId != e.prefabId) {
                reset();
                empties[e.prefabId].SetActive(true);
            }
        }
        setNotOnFire(); // Todo use fire from response
        Self = unit;
    }

    void reset() {
        setNotOnFire();
        setNotTargettable();
        player.SetActive(false);
        hpText.text = "";
        foreach(var o in npcs) {
            o.SetActive(false);
        }
        foreach(var o in empties) {
            o.SetActive(false);
        }

    }

    // Update is called once per frame
    void Update()
    {
        if (Self is NPCUnit) {
            hpText.text = (Self as NPCUnit).health.ToString();
        }
        if (Self is PlayerUnit) {
            hpText.text = (Self as PlayerUnit).health.ToString();
        }

    }
    public void setOnFire() {
        onFire.SetActive(true);
    }

    public void setTargettable() {
        targettable.SetActive(true);
    }
    
    public void setNotOnFire() {
        onFire.SetActive(false);
    }

    public void setNotTargettable() {
        targettable.SetActive(false);
        whenSkillUse = Option<Skill>.None;
    }

    public void onClick(Skill skill) {
        whenSkillUse = Option<Skill>.Some(skill);
    }

    void OnMouseDown() {
        if (whenSkillUse.isSome) {
            gm.SkillWasUsed(index, whenSkillUse.value);
        }
    }
}
