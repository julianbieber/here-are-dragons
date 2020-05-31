using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Text;

public class CharacterViewController : MonoBehaviour
{
    public Text characterText;

    private int nextUpdate=1;

    // Start is called before the first frame update 
    void Start()
    {
        
    }

    // Update is called once per frame
    async void Update()
    {
        if (Time.time >= nextUpdate) {
            nextUpdate=Mathf.FloorToInt(Time.time)+1;
            var character = await CharacterAPI.getCharacter();
            var characterStringBuilder = new StringBuilder();
            characterStringBuilder.Append("Ranger: ");
            characterStringBuilder.Append(character.rangerExperience);
            characterStringBuilder.Append("\n");
            characterStringBuilder.Append("Sorcerer: ");
            characterStringBuilder.Append(character.sorcererExperience);
            characterStringBuilder.Append("\n");
            characterStringBuilder.Append("Warrior: ");
            characterStringBuilder.Append(character.warriorExperience);
            characterText.text = characterStringBuilder.ToString();
        }
        
    }
}
