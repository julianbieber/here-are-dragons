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

public class FulleListeMitQuests : MonoBehaviour
{
    // Die Drpodownlist d ist die Dropdownliste in der die noch zu erreichenden Quests, ausgewählt werden können.
    public Dropdown d;
    //newList ist die Liste der Quests, von denen in diesem Moment ausgewählt werden kann.
    public GameObject panel;
    private bool activeQuest= false;
    private List<DAOQuest> newList = new List<DAOQuest>();
    private List<long> questIds = new List<long>();

    private bool waitParameter = false;
    public Text text;

    private int timeOfText;


    public GameObject Player;
    /*
        Die Methode Awake wird aufgerufen, wenn das Panel aktiviert wird, auf der sich die DropdownListe befindent.
        Die Methode Awake ruft die Methode FillList auf.
    */
    async void Awake()
    {
        FillList();
    }
    /*
        Die Methode FillList füllt die Felder der DropDownlist auf dem Panel mit allen Quests. An erster Stelle der Liste 
        gibt es ein Auswhlfeld "kein Quest", falls der Spieler kein Quest auswählen möchte. Dazu verwendet die Methode 
        FillList die Methode get Filling
    */
    async void FillList()
    {
        List<String> Quest = new List<String>() { "kein Quest" };
        List<String> Quets = await getFilling();
        d.ClearOptions();
        d.AddOptions(Quest);
        d.AddOptions(Quets);
    }
    /*
        OnEnable wird einmal aufgerufen, wenn die Dropdownliste der Quests aktiviert wird.
    */
    async public void OnEnable(){

        int selectedQuestIndex = d.GetComponent<Dropdown>().value;
        List<Dropdown.OptionData> questOptions = d.GetComponent<Dropdown>().options;

        var activeQuestInGroup = await QuestAPI.getActiveQuestInGroup();
        if (activeQuestInGroup.activ){
            questOptions[0].text = "Gruppenmitglied hat bereits Quest ausgewählt";
        }else{
            questOptions[0].text = "kein Quest";
        }
        activeQuest = activeQuestInGroup.activ;
        for (int i = 1 ; i<questOptions.Count && newList.Count>0 && i != selectedQuestIndex ; i++){

            DAOQuest q = newList[i-1];
            Quest quest = new Quest(Option<DAOQuest>.Some(q),Player);

            var diffi = await QuestAPI.getDifficulty(q.questID);
            double distance = diffi.difficulty;

            int indexFirstTab = questOptions[i].text.IndexOf("|");

            if(indexFirstTab!=-1){
                questOptions[i].text = distance + questOptions[i].text.Substring(indexFirstTab);
            }
        }
    }
    /*
        Die Methode getFilling erhält die Quests in der Nähe vom Server und hat als Augabewert eine TaksList von Stings, die
        die Quest IDs erhält.
    */
    async public Task<List<String>> getFilling()
    {
        //TODO: Erstelle eine Funktion, mit der nurnoch Quests ausgewählt werden, in einem anulus von der Position des Spielers
        List<String> Quets = new List<String>();
        waitParameter=true;
        var I = await QuestAPI.getListOfQuestsNearby(900000000);
        newList = I.quests;
        waitParameter=false;
        questIds.Clear();
        questIds.Add(-1);
        foreach (DAOQuest q in newList)
        {
            Quest quest = new Quest(Option<DAOQuest>.Some(q),Player);
            waitParameter=true;
            var diffi = await QuestAPI.getDifficulty(q.questID);
            waitParameter=false;
            double distance = diffi.difficulty;

            if (questInBestimmtenAbstand(q)& q.tag != null)
            {
                Quets.Add(distance+"|"+q.tag+"|"+typeOfPoI(q.priority));
            }
            if(questInBestimmtenAbstand(q)& q.tag == null)
            {
                Quets.Add(distance+"|"+typeOfPoI(q.priority));
            }
            questIds.Add(q.questID);
        }
        return Quets;
    }

    private String typeOfPoI(float priority)
    {
        if(Math.Floor(priority).Equals(1)) return "streets and footway points";
        if(Math.Floor(priority).Equals(2)) return "trees, stones and springs";
        if(Math.Floor(priority).Equals(3)) return "wells, towers and survey points";
        if(Math.Floor(priority).Equals(5)) return "public communication";
        if(Math.Floor(priority).Equals(6)) return "benches";
        if(Math.Floor(priority).Equals(7)) return "pedestrian walkways";
        if(Math.Floor(priority).Equals(8)) return "parks";
        if(Math.Floor(priority).Equals(9)) return "stations and stops for means of transport";
        if(Math.Floor(priority).Equals(10)) return "product shops and services";
        if(Math.Floor(priority).Equals(11)) return "healthcare and social facilities";
        if(Math.Floor(priority).Equals(12)) return "places for food or drinks";
        if(Math.Floor(priority).Equals(13)) return "places for doing sport";
        if(Math.Floor(priority).Equals(14)) return "educational establishments";
        if(Math.Floor(priority).Equals(15)) return "playgrounds";
        if(Math.Floor(priority).Equals(16)) return "mountain peaks";
        if(Math.Floor(priority).Equals(17)) return "huts and other shelters";
        if(Math.Floor(priority).Equals(18)) return "places for picnic or barbecue";
        if(Math.Floor(priority).Equals(19)) return "libraries and public bookcases";
        if(Math.Floor(priority).Equals(20)) return " town halls";
        if(Math.Floor(priority).Equals(21)) return "places of the categories entertainment, arts, culture and tourism";
        if(Math.Floor(priority).Equals(22)) return "places of worship";
        if(Math.Floor(priority).Equals(23)) return "historic places";
        return "";
    }

    private bool questInBestimmtenAbstand(DAOQuest q)
    {
        Quest a = new Quest(Option<DAOQuest>.Some(q), Player);
        //if (a.anulus())
        //{
            return true;
        //}
        //return false;
    }

    /*
        Update gibt setzt die globale Variable @ausgewählterQuest auf den Quest, der von Spieler ausgewählt wurde. Wurde kein Quest
        ausgewählt, wird null übergeben.
    */
    async void Update()
    {       
        if(!Global.erledigt.value&&!waitParameter&&!activeQuest){
            Global.ausgewahlterQuest = getSelectedQuest();
        }
        if (Global.erledigt.value&&!waitParameter)
        {
            Global.erledigt = Option<bool>.Some(false);
            FillList();
        }
    }
    /*
        Die Methode getSelected Quest gibt den von Spieler ausgewählten Quest in einer Methode aus.
    */
    private Option<DAOQuest> getSelectedQuest()
    {
        
        int menuIndex = d.GetComponent<Dropdown>().value;
        List<Dropdown.OptionData> menuOptions = d.GetComponent<Dropdown>().options;
        string value = menuOptions[menuIndex].text;
        

        foreach (DAOQuest q in newList)
        {
            if (questIds[menuIndex].ToString().Equals(q.questID.ToString()))
            {
                Global.difficulty = Option<int>.Some(int.Parse(value.Substring(0,value.IndexOf("|"))));
                return Option<DAOQuest>.Some(q);
            }
        }
        return Option<DAOQuest>.None;
    }
}
