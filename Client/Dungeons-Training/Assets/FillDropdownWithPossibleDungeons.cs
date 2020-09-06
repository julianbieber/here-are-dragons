using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Threading.Tasks;
using UnityEngine.Networking;
using Mapbox.Unity.Utilities;
using Mapbox.Utils;
using Mapbox.Unity.Location;
using System.Linq;
public class FillDropdownWithPossibleDungeons : MonoBehaviour
{
    public Dropdown d;
    // Start is called before the first frame update
    void Start()
    {
        FillList();
    }

    // Update is called once per frame
    void Update()
    {
        //FillList();
    }
     async void FillList()
    {
        DifficultyResponse Dungeon = await getDungeons();
        List<String> dungeonsIdList = new List<String>();
        foreach(var completedDungeon in Dungeon.difficulties){
            var GroupMembers="";
            if(completedDungeon.members.Count>1) {
                GroupMembers =  "| Group: "+String.Join(", ",completedDungeon.members);
            }
            dungeonsIdList.Add(completedDungeon.id.ToString()+"| Difficulty: "+completedDungeon.difficulty.ToString()+GroupMembers);
        }
        d.ClearOptions();
        d.AddOptions(new List<String>() { "" });
        d.AddOptions(dungeonsIdList);
    }

    async public Task<DifficultyResponse> getDungeons()
    {
        //TODO: Erstelle eine Funktion, mit der nurnoch Quests ausgewählt werden, in einem anulus von der Position des Spielers
        var I = await DifficultyAPI.getAvailableDungeons(); 
        return I;
    }

}
