using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.Linq;

public class LoadTalentTree : MonoBehaviour
{
    public Canvas canvas;
    public GameObject UnlockButtonPrefab;
    public GameObject TreeNodePrefab; 
    private Option<TalentResponse> currentTalentTree;
    private List<GameObject> displayedUnlockButtons;
    private float screenWidth;
    private float screenHeight;
    public int unlockButtonSize;
    public Text tooltip;

    
    
    // Start is called before the first frame update
    async void Start()
    {
        var unlockOptions = new List<Talent>();

        unlockOptions.Add(new Talent{});
        unlockOptions.Add(new Talent{});
        unlockOptions.Add(new Talent{});
        
        //currentTalentTree = await TalentAPI.getTalents();
        currentTalentTree = Option<TalentResponse>.Some(
            new TalentResponse {
                unlocking = null,
                unlockOptions = unlockOptions
            }
        );
        
        screenHeight = canvas.GetComponent<RectTransform>().rect.height;
        screenWidth = canvas.GetComponent<RectTransform>().rect.width;
        
        displayedUnlockButtons = new List<GameObject>();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public async void load(int activityId) {
        var apiTalentsO = await TalentAPI.getTalents();
        if (apiTalentsO.isSome) {
            currentTalentTree = apiTalentsO;
        }
        if (currentTalentTree.isSome) {
            
            var tree = currentTalentTree.value;
            
            var relevantUnlocks = tree.unlockOptions.Where(t => t.activityId == activityId).ToList();
            
            for (int i = 0; i < relevantUnlocks.Count; ++i) {
                if (i >= displayedUnlockButtons.Count) {
                    displayedUnlockButtons.Add(Instantiate(UnlockButtonPrefab));
                }
                displayedUnlockButtons[i].transform.SetParent(canvas.transform);
                displayedUnlockButtons[i].transform.SetAsLastSibling();
                var displayButton = displayedUnlockButtons[i].GetComponent<TalentDisplay>();
                displayButton.tooltip = tooltip;
                displayButton.setTalent(relevantUnlocks[i]);
            }
            var numberOfButtons = displayedUnlockButtons.Count;
            for (int i = relevantUnlocks.Count; i < numberOfButtons; ++i) {
                displayedUnlockButtons[displayedUnlockButtons.Count - 1].Destroy();
                displayedUnlockButtons.RemoveAt(displayedUnlockButtons.Count - 1);
            }
            
            if (displayedUnlockButtons.Count % 2 == 0) {
                for (int i = 0; i < displayedUnlockButtons.Count / 2; ++i) {
                    displayedUnlockButtons[i].transform.localPosition = new Vector3(- unlockButtonSize * (displayedUnlockButtons.Count / 2 - 1) + i * unlockButtonSize - unlockButtonSize  /2, - 750,0);
                }
                for (int i = displayedUnlockButtons.Count / 2; i < displayedUnlockButtons.Count; ++i) {
                    displayedUnlockButtons[i].transform.localPosition = new Vector3((i-  displayedUnlockButtons.Count/2) * unlockButtonSize + unlockButtonSize  /2, -750,0);
                }
            } else {
                for (int i = 0; i < displayedUnlockButtons.Count; ++i) {
                    displayedUnlockButtons[i].transform.localPosition = new Vector3(-unlockButtonSize * (displayedUnlockButtons.Count / 2) + i * unlockButtonSize, - 750,0);
                }
            }

            
        }
    }
}
