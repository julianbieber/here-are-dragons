using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;

public class DungeonUnit : MonoBehaviour
{
    public GameObject onFirePrefab;
    public GameObject targettablePrefab;
    public Unit Self { get; set; }
    public int index;
    public TextMesh hpText;

    public DungeonController gm;

    private GameObject onFireObject;
    private GameObject targettableObject;
    // Start is called before the first frame update
    private List<DungeonUnit> onCastEffected = new List<DungeonUnit>();
    private Option<Skill> whenSkillUse = Option<Skill>.None;
    void Start()
    {
        onFireObject = Instantiate(onFirePrefab);
        setNotOnFire();
        targettableObject = Instantiate(targettablePrefab);
        setNotTargettable();
    }

    // Update is called once per frame
    void Update()
    {
        onFireObject.transform.position = transform.position;
        onFireObject.transform.localScale = transform.localScale;

        targettableObject.transform.position = transform.position;
        targettableObject.transform.localScale = transform.localScale;

        if (Self is NPCUnit) {
            hpText.text = (Self as NPCUnit).health.ToString();
        }
        if (Self is PlayerUnit) {
            hpText.text = (Self as PlayerUnit).health.ToString();
        }

    }

    public void setOnFire() {
        onFireObject.SetActive(true);
    }

    public void setTargettable() {
        targettableObject.SetActive(true);
    }
    
    public void setNotOnFire() {
        onFireObject.SetActive(false);
    }

    public void setNotTargettable() {
        targettableObject.SetActive(false);
        whenSkillUse = Option<Skill>.None;
        onCastEffected = new List<DungeonUnit>();
    }

    public void onClick(List<DungeonUnit> effected, Skill skill) {
        whenSkillUse = Option<Skill>.Some(skill);
        onCastEffected = effected;
    }

    void OnMouseDown() {
        if (whenSkillUse.isSome) {
            var skill = whenSkillUse.value;
            if (skill.burnDuration > 0) {
                foreach (var u in onCastEffected) {
                    u.setOnFire();
                }    
            }
            foreach (var u in onCastEffected) {
                if (u.Self is NPCUnit) {
                    (u.Self as NPCUnit).health -= skill.damage;
                }
                if (u.Self is PlayerUnit) {
                    (u.Self as PlayerUnit).health -= skill.damage;
                }
            }
            gm.SkillWasUsed(index, whenSkillUse.value);
        }
    }
}
