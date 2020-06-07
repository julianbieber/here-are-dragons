using System.Collections;
using System.Collections.Generic;
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
        dungeon.units.Add(new PlayerUnit{userId = Global.userId.value});
        dungeon.units.Add(new EmptyUnit{ prefabId = 0 });
        dungeon.units.Add(new NPCUnit{ prefabId = 0});
        dungeon.units.Add(new NPCUnit{ prefabId = 0});
        dungeon.units.Add(new NPCUnit{ prefabId = 0});
        dungeon.units.Add(new NPCUnit{ prefabId = 0});
    }

    // Update is called once per frame
    void Update()
    {
        if (objects.Count != dungeon.units.Count) {
            foreach(var o in objects) {
                o.Destroy();
            }
            objects.Clear();

            foreach(var unit in dungeon.units) {
                if (unit is PlayerUnit) {
                    objects.Add(Instantiate(playerPrefab));
                }
                if (unit is NPCUnit) {
                    objects.Add(Instantiate(npcPrefabs[((NPCUnit)unit).prefabId]));
                }
                if (unit is EmptyUnit) {
                    objects.Add(Instantiate(emptyPrefabs[((EmptyUnit)unit).prefabId]));
                }
            }

            var objectSize = (screenWidth ) / (float)(objects.Count + 2);

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
    }

    public void makeTargettableForPattern(string pattern) {
        var selfUnitIndex = getSelf();
        if (selfUnitIndex != -1) {
            var selfInPattern = pattern.Length / 2;
            for(int patternI = 0; patternI < pattern.Length; ++patternI) {
                if (pattern[patternI] == '1'){
                    var duO = calculateUnitIndex(pattern, selfUnitIndex, patternI);
                    if (duO.isSome) {
                        DungeonUnit du = duO.value;
                        du.setTargettable();
                    }
                } else {
                    var duO = calculateUnitIndex(pattern, selfUnitIndex, patternI);
                    if (duO.isSome) {
                        DungeonUnit du = duO.value;
                        du.setNotTargettable();
                    }
                }
                
            }
        }
    }

    private Option<DungeonUnit> calculateUnitIndex(string pattern, int playerIndex, int patternIndex) {
        var i = playerIndex + patternIndex - pattern.Length / 2;
        if (i >= 0 && i < objects.Count) {
            return Option<DungeonUnit>.Some(objects[i].GetComponent<DungeonUnit>());
        } else {
            return Option<DungeonUnit>.None;
        }
    }

    private int getSelf() {
        for(int i = 0; i < dungeon.units.Count; ++i) {
            var u = dungeon.units[i];
            if (u is PlayerUnit && ((PlayerUnit)u).userId == Global.userId.value) {
                return i;
            }
        }
        return -1;
    }

    public void endTurn() {

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