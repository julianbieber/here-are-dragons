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

    public GameObject joinButton;
    public GameObject openButton;
    public InputField questIdInputField;
    public GameObject questIdInputObject;

    private Turn currentTurn;

    private Option<int> dungeonId = Option<int>.None;
    private bool waitForTurn = true;

    private int remainingAP;

    private List<GameObject> objects = new List<GameObject>();
    private Vector3 lowerLeft;
    private Vector3 lowerRight;
    private Vector3 topLeft;
    private float screenWidth;
    private float screenHeight;

    private int nextUpdate = 1; 
    // Start is called before the first frame update
    void Start()
    {

        lowerLeft = mainCamera.ViewportToWorldPoint(new Vector3(0,0,0));
 
        lowerRight = mainCamera.ViewportToWorldPoint(new Vector3(1,0,0));
        screenWidth = (lowerRight.x - lowerLeft.x);

        topLeft = mainCamera.ViewportToWorldPoint(new Vector3(0, 1, 0));
        screenHeight = topLeft.y - lowerLeft.y;

        currentTurn = new Turn{ turnId = 0, skillsUsed = new List<SkillUsage>() };
    }

    public async void openDungeon() {
        var dungeonO = await DungeonAPI.openDungeon(Int32.Parse(questIdInputField.text));
        if (dungeonO.isSome) {
            var dungeon = dungeonO.value;
            setDungeon(dungeon);
            joinButton.SetActive(false);
            openButton.SetActive(false);
            questIdInputObject.SetActive(false);
        }
    }

    public async void joinDungeon() {
        // TODO 
    }

    // Update is called once per frame
    async void Update()
    {
        if (dungeonId.isSome && waitForTurn) {
            if(Time.time>=nextUpdate) {
                nextUpdate=Mathf.FloorToInt(Time.time)+2;
                var dungeonO = await DungeonAPI.getDungeon(dungeonId.value);
                if (dungeonO.isSome) {
                    setDungeon(dungeonO.value);
                }
            }
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
        remainingAP = dungeon.ap;
        waitForTurn = !dungeon.myTurn;
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
        if (dungeonId.isSome) {
            DungeonAPI.endTurn(dungeonId.value, currentTurn);
            currentTurn = new Turn{
                skillsUsed = new List<SkillUsage>(),
                turnId = 0
            };
            waitForTurn = true;
        }
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
    public bool myTurn;
    public int ap;
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

[Serializable]
public class Turn {
    public int turnId;
    public List<SkillUsage> skillsUsed;
}

[Serializable]
public class SkillUsage {
    public int targetId;
    public Skill skill;
}