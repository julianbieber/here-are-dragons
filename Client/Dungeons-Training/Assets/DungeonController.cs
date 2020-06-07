using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.UI;

public class DungeonController : MonoBehaviour
{
    public Camera mainCamera;
    public List<GameObject> emptyPrefabs;
    public GameObject playerPrefab;

    public List<GameObject> npcPrefabs;

    public Text apText;

    private Dungeon dungeon = new Dungeon {
        units = new List<Unit>()
    };

    private Turn currentTurn;

    private int remainingAP;

    private List<GameObject> objects = new List<GameObject>();
    private Vector3 lowerLeft;
    private Vector3 lowerRight;
    private Vector3 topLeft;
    private float screenWidth;
    private float screenHeight;
    // Start is called before the first frame update
    void Start()
    {

        lowerLeft = mainCamera.ViewportToWorldPoint(new Vector3(0,0,0));
 
        lowerRight = mainCamera.ViewportToWorldPoint(new Vector3(1,0,0));
        screenWidth = (lowerRight.x - lowerLeft.x);

        topLeft = mainCamera.ViewportToWorldPoint(new Vector3(0, 1, 0));
        screenHeight = topLeft.y - lowerLeft.y;

        dungeon.units.Add(new EmptyUnit{ prefabId = 0 });
        dungeon.units.Add(new PlayerUnit{userId = Global.userId.value, health = 100});
        dungeon.units.Add(new EmptyUnit{ prefabId = 0 });
        dungeon.units.Add(new NPCUnit{ prefabId = 0, health = 100});
        dungeon.units.Add(new NPCUnit{ prefabId = 0, health = 100});
        dungeon.units.Add(new NPCUnit{ prefabId = 0, health = 100});
        dungeon.units.Add(new NPCUnit{ prefabId = 0, health = 100});

        currentTurn = new Turn{ turnId = 0, skillsUsed = new List<SkillUsage>() };
    }

    // Update is called once per frame
    void Update()
    {
        if (objects.Count != dungeon.units.Count) {
            setDungeon(dungeon);
        }

        apText.text = remainingAP.ToString();
    }

    void setDungeon(Dungeon dungeon) {
        foreach(var o in objects) {
                o.Destroy();
            }
            objects.Clear();
            var index = 0;
            foreach(var unit in dungeon.units) {
                if (unit is PlayerUnit) {
                    var o = Instantiate(playerPrefab);
                    o.GetComponent<DungeonUnit>().Self = unit;
                    o.GetComponent<DungeonUnit>().gm = this;
                    o.GetComponent<DungeonUnit>().index = index;
                    objects.Add(o);
                }
                if (unit is NPCUnit) {
                    var o = Instantiate(npcPrefabs[((NPCUnit)unit).prefabId]);
                    o.GetComponent<DungeonUnit>().Self = unit;
                    o.GetComponent<DungeonUnit>().gm = this;
                    o.GetComponent<DungeonUnit>().index = index;
                    objects.Add(o);
                }
                if (unit is EmptyUnit) {
                    var o = Instantiate(emptyPrefabs[((EmptyUnit)unit).prefabId]);
                    o.GetComponent<DungeonUnit>().Self = unit;
                    o.GetComponent<DungeonUnit>().gm = this;
                    o.GetComponent<DungeonUnit>().index = index;
                    objects.Add(o);
                }
                ++index;
            }

            var objectSize = screenWidth / (float)(objects.Count + 2);

            if (objectSize > screenHeight * 0.7f){
                objectSize = screenHeight * 0.7f;
            }

            var objectLeft = new Vector3(lowerLeft.x  + objectSize * 0.5f, 0, 0);
            var objectRight = new Vector3(lowerRight.x - objectSize * 0.5f, 0, 0);

            for (int i = 0; i < objects.Count; ++i) {
                objects[i].transform.localPosition = Vector3.Lerp(objectLeft, objectRight, (i)/((float)objects.Count-1));
                objects[i].transform.localScale = new Vector3( objectSize, objectSize, 1);
            }
    }

    public void makeTargettableForPattern(Skill skill) {
        if (skill.apCost <= remainingAP) {
            var selfUnitIndex = getSelf();
            if (selfUnitIndex != -1) {
                var selfInPattern = skill.targetPattern.Length / 2;
                for(int patternI = 0; patternI < skill.targetPattern.Length; ++patternI) {
                    if (skill.targetPattern[patternI] == '1'){
                        var duO = calculateUnitIndex(skill.targetPattern, selfUnitIndex, patternI);
                        if (duO.isSome) {
                            DungeonUnit du = duO.value.Item1;
                            du.setTargettable();
                            du.onClick(identifyEffected(skill.effectPattern, duO.value.Item2), skill);
                        }
                    } else {
                        var duO = calculateUnitIndex(skill.targetPattern, selfUnitIndex, patternI);
                        if (duO.isSome) {
                            DungeonUnit du = duO.value.Item1;
                            du.setNotTargettable();
                        }
                    }
                    
                }
            }
        }
    }

    public List<DungeonUnit> identifyEffected(string effectPattern, int targetIndex) {
        var units = new List<DungeonUnit>();
        for (int i = 0; i < effectPattern.Length; ++i) {
            int dungeonIndex = targetIndex + i - effectPattern.Length / 2;
            if (dungeonIndex >= 0&& dungeonIndex < objects.Count) {
                units.Add(objects[dungeonIndex].GetComponent<DungeonUnit>());
            }
        }
        return units;
    }

    private Option<Tuple<DungeonUnit, int>> calculateUnitIndex(string pattern, int playerIndex, int patternIndex) {
        var i = playerIndex + patternIndex - pattern.Length / 2;
        if (i >= 0 && i < objects.Count) {
            return Option<Tuple<DungeonUnit, int>>.Some(new Tuple<DungeonUnit, int>(objects[i].GetComponent<DungeonUnit>(), i));
        } else {
            return Option<Tuple<DungeonUnit, int>>.None;
        }
    }

    private int getSelf() {
        for(int i = 0; i < objects.Count; ++i) {
            var u = objects[i].GetComponent<DungeonUnit>().Self;
            if (u is PlayerUnit && ((PlayerUnit)u).userId == Global.userId.value) {
                return i;
            }
        }
        return -1;
    }

    public void endTurn() {
        remainingAP += 10;
    }

    public void SkillWasUsed(int unitIndex, Skill skill) {
        foreach(var du in objects) {
            du.GetComponent<DungeonUnit>().setNotTargettable();
        }
        currentTurn.skillsUsed.Add(new SkillUsage{ targetId = unitIndex, skill = skill});
        remainingAP -= skill.apCost;
    }
}

public class Dungeon {
    public List<Unit> units;
}

public interface Unit {

}

public class PlayerUnit : Unit {
    public int userId;
    public int health;
}

public class NPCUnit : Unit {
    public int prefabId;
    public int health;
}

public class EmptyUnit : Unit {
    public int prefabId;
}

public class Turn {
    public int turnId;
    public List<SkillUsage> skillsUsed;
}

public class SkillUsage {
    public int targetId;
    public Skill skill;
}