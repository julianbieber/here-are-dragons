using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class DungeonUnit : MonoBehaviour
{
    public GameObject onFire;
    public GameObject wet;
    public GameObject shock;
    public GameObject stun;
    public GameObject targettable;
    public List<GameObject> empties;
    public List<GameObject> npcs;
    public List<GameObject> player;
    public Unit Self;
    public int index;
    public TextMesh hpText;

    public DungeonController gm;

    // Start is called before the first frame update
    
    private Option<Skill> whenSkillUse = Option<Skill>.None;
    void Start()
    {
        //reset();
    }

    public void make(Unit unit) {
        reset();
        if (unit is PlayerUnit) {
            var p = unit as PlayerUnit;
            if (p.skills.Count > 0) {
                var firstSkill = p.skills[0];
                if (firstSkill.dexterityScaling >= firstSkill.strengthScaling && firstSkill.dexterityScaling >= firstSkill.spellPowerScaling) {
                    player[0].SetActive(true);
                } else if (firstSkill.strengthScaling >= firstSkill.dexterityScaling && firstSkill.strengthScaling >= firstSkill.spellPowerScaling) {
                    player[1].SetActive(true);
                } else if (firstSkill.spellPowerScaling >= firstSkill.dexterityScaling && firstSkill.spellPowerScaling >= firstSkill.strengthScaling) {
                    player[2].SetActive(true);
                }
            } else {
                player[0].SetActive(true);
            }
            displayStatus(p.status);
            hpText.text = p.health.ToString();
        } 
        if (unit is NPCUnit) {
            var n = unit as NPCUnit;
            npcs[n.prefabId].SetActive(true);
            hpText.text = n.health.ToString();
            displayStatus(n.status);
        }
        if (unit is EmptyUnit) {
            var e = unit as EmptyUnit;
            empties[e.prefabId].SetActive(true);
            displayStatus(e.status);
        }
        Self = unit;
    }

    void reset() {
        onFire.SetActive(false);
        wet.SetActive(false);
        shock.SetActive(false);
        stun.SetActive(false);
        setNotTargettable();
        foreach(var o in player) {
            o.SetActive(false);
        }
        hpText.text = "";
        foreach(var o in npcs) {
            o.SetActive(false);
        }
        foreach(var o in empties) {
            o.SetActive(false);
        }
    }

    void displayStatus(Status status) {
        if (status.burning > 0 ){
            onFire.SetActive(true);
        }
        if (status.wet > 0) {
            wet.SetActive(true);
        }
        if (status.shocked > 0) {
            shock.SetActive(true);
        }
        if (status.stunned > 0) {
            stun.SetActive(true);
        } 
        if (status.knockedDown > 0) {
            transform.eulerAngles = new Vector3(
                0,
                0,
                90
            ); 
        } else {
            transform.eulerAngles = new Vector3(
                0,
                0,
                0
            );
        }
    }

    // Update is called once per frame
    void Update()
    {

    }
    public void setOnFire() {
        onFire.SetActive(true);
    }

    public void setTargettable() {
        targettable.SetActive(true);
    }
    
    public void setNotOnFire() {
        
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
