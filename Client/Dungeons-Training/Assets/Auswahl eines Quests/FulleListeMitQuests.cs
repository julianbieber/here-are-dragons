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
    private List<DAOQuest> newList = new List<DAOQuest>();
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
        Die Methode getFilling erhält die Quests in der Nähe vom Server und hat als Augabewert eine TaksList von Stings, die
        die Quest IDs erhält.
    */
    async Task<List<String>> getFilling()
    {
        //TODO: Erstelle eine Funktion, mit der nurnoch Quests ausgewählt werden, in einem anulus von der Position des Spielers
        List<String> Quets = new List<String>();
        var I = await QuestAPI.getListOfQuestsNearby(900000000);
        newList = I.quests;
        foreach (DAOQuest q in newList)
        {
            if (questInBestimmtenAbstand(q))
            {
                Quets.Add(q.questID.ToString());
            }
        }
        return Quets;
    }

    private bool questInBestimmtenAbstand(DAOQuest q)
    {
        Quest a = new Quest(Option<DAOQuest>.Some(q), Player);
        if (a.anulus())
        {
            return true;
        }
        return false;
    }

    /*
        Update gibt setzt die globale Variable @ausgewählterQuest auf den Quest, der von Spieler ausgewählt wurde. Wurde kein Quest
        ausgewählt, wird null übergeben.
    */
    async void Update()
    {
        Global.ausgewahlterQuest = getSelectedQuest();
        if (Global.ausgewahlterQuest.isSome && Global.ausgewahlterQuest.value.erledigt)
        {
            await getFilling();
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
            if (q.questID.ToString().Equals(value))
            {

                return Option<DAOQuest>.Some(q);
            }
        }
        return Option<DAOQuest>.None;
    }
}
