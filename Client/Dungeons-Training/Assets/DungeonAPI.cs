using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine;
using System.Linq;

public static class DungeonAPI
{
    public static async Task<List<int>> getAvailableDungeonIds() {
        var response = await API.get<AvailableDungeonsResponse>("dungeons", new Dictionary<string, string>());
        if (response.isSome) {
            return response.value.ids;
        } else {
            Debug.Log("Failed to retrieve available dungeons");
            return new List<int>();
        }
    }

    public static async Task<Option<Dungeon>> openDungeon(int questId) {
        var request = new OpenDungeonRequest{ questId = questId };
        var response = await API.put<OpenDungeonRequest, DungeonResponse>("dungeon", request, new Dictionary<string, string>());

        if (response.isSome) {
            return Option<Dungeon>.Some(convertDungeonResponse(response.value));
        } else {
            Debug.Log("failed to open dungeon");
            return Option<Dungeon>.None;
        }
    }

    public static async void endTurn(int dungeonId, Turn turn) {
        await API.post<Turn, string>("dungeon/" + dungeonId, turn, new Dictionary<string, string>());
    }

    public static async Task<Option<Dungeon>> getDungeon(int dungeonId) {
        var response = await API.get<DungeonResponse>("dungeon/" + dungeonId, new Dictionary<string, string>());
        if (response.isSome) {
            return Option<Dungeon>.Some(convertDungeonResponse(response.value));
        } else {
            Debug.Log("Failed to retrieve dungeon for id: " + dungeonId);
            return Option<Dungeon>.None;
        }
    }

    static Dungeon convertDungeonResponse(DungeonResponse response) {
        return new Dungeon {
            units = response.units.Select(u => convertUnitResponse(u)).ToList(),
            myTurn = response.myTurn,
            ap = response.ap
        };
    }

    static Unit convertUnitResponse(UnitResponse response) {
        if (response.tyype == "empty") {
            return new EmptyUnit {
                prefabId = response.prefabId
            };
        } else if (response.tyype == "npc") {
            return new NPCUnit {
                prefabId = response.prefabId,
                health = response.health
            };
        } else if (response.tyype == "player") {
            return new PlayerUnit {
                userId = response.userId,
                health = response.health
            };
        } else {
            throw new System.ArgumentException("Unrecognized unit type", response.tyype);
        }
    }
}

[Serializable]
public class AvailableDungeonsResponse {
    public List<int> ids;
}

[Serializable]
public class OpenDungeonRequest {
    public int questId;
}

[Serializable]
public class DungeonResponse {
    public List<UnitResponse> units;
    public bool myTurn;
    public int ap;
}

public class UnitResponse {
    public string tyype;
    public int userId;
    public int health;
    public int prefabId;
}
