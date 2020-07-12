using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class AttributeControll : MonoBehaviour
{
    public Button addButton;
    public Button subButton;
    public Button unlockButton;
    public Text text;
    public string baseText;
    public Attributes addDiff;

    // Start is called before the first frame update
    void Start()
    {
        unlockButton.gameObject.SetActive(false);
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void display(PlayerCharacter character) {

        if (
            (character.canUnlockWarrior && (addDiff.strength == 1 || addDiff.constitution == 1)) ||
            (character.canUnlockSorcerer && (addDiff.spellPower == 1 || addDiff.willPower == 1)) ||
            (character.canUnlockRanger && (addDiff.dexterity == 1 || addDiff.evasion == 1)) 
        ) {
            unlockButton.gameObject.SetActive(true);
        } else {
            unlockButton.gameObject.SetActive(false);
        }
        if (addDiff.strength == 1) {
            text.text = baseText + character.selectedAttributes.strength + "/" + character.unlockedAttributes.strength;
            subButton.gameObject.SetActive(character.selectedAttributes.strength > 0);
        }
        if (addDiff.constitution == 1) {
            text.text = baseText + character.selectedAttributes.constitution + "/" + character.unlockedAttributes.constitution;
            subButton.gameObject.SetActive(character.selectedAttributes.constitution > 0);
        }
        if (addDiff.spellPower == 1) {
            text.text = baseText + character.selectedAttributes.spellPower + "/" + character.unlockedAttributes.spellPower;
            subButton.gameObject.SetActive(character.selectedAttributes.spellPower > 0);
        }
        if (addDiff.willPower == 1) {
            text.text = baseText + character.selectedAttributes.willPower + "/" + character.unlockedAttributes.willPower;
            subButton.gameObject.SetActive(character.selectedAttributes.willPower > 0);
        }
        if (addDiff.dexterity == 1) {
            text.text = baseText + character.selectedAttributes.dexterity + "/" + character.unlockedAttributes.dexterity;
            subButton.gameObject.SetActive(character.selectedAttributes.dexterity > 0);
        }
        if (addDiff.evasion == 1) {
            text.text = baseText + character.selectedAttributes.evasion + "/" + character.unlockedAttributes.evasion;
            subButton.gameObject.SetActive(character.selectedAttributes.evasion > 0);
        }
        addButton.gameObject.SetActive(character.maxSelectableAttributes > character.selectedAttributes.sum());
        
    }

    public void add() {
        CharacterAPI.selectAttributes(addDiff);
    }

    public void sub() {
        Attributes diff = new Attributes {
            strength = addDiff.strength * -1,
            constitution = addDiff.constitution * -1,
            spellPower = addDiff.spellPower * -1,
            willPower = addDiff.willPower * -1,
            dexterity = addDiff.dexterity * -1,
            evasion = addDiff.evasion * -1,
        };
        CharacterAPI.selectAttributes(diff);
    }

    public void unlock() {
        CharacterAPI.unlockAttribute(addDiff);
    }
}
