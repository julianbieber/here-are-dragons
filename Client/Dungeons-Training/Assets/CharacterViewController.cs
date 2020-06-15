using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Text;

public class CharacterViewController : MonoBehaviour
{
    public Text characterText;
    public Character character;

    private int nextUpdate=1;

    // Start is called before the first frame update 
    void Start()
    {
        
    }

    // Update is called once per frame
    async void Update()
    {
        if (character.player.isSome) {
            var characterStringBuilder = new StringBuilder();
            characterStringBuilder.Append("Ranger: ");
            characterStringBuilder.Append(character.player.value.rangerExperience);
            characterStringBuilder.Append("\n");
            characterStringBuilder.Append("Sorcerer: ");
            characterStringBuilder.Append(character.player.value.sorcererExperience);
            characterStringBuilder.Append("\n");
            characterStringBuilder.Append("Warrior: ");
            characterStringBuilder.Append(character.player.value.warriorExperience);
            characterText.text = characterStringBuilder.ToString(); 
        }
    }
}
