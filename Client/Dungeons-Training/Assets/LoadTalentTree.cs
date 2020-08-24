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
    private List<GameObject> displayedGroupUnlockButtons;
    public GameObject currentUnlock;
    public GameObject currentUnlockGroup;
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
        displayedGroupUnlockButtons = new List<GameObject>();

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
                displayButton.loadTalentTree = this;
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
                    displayedUnlockButtons[i].transform.localPosition = new Vector3(- unlockButtonSize * (displayedUnlockButtons.Count / 2 - 1) + i * unlockButtonSize - unlockButtonSize  /2, - 550,0);
                }
                for (int i = displayedUnlockButtons.Count / 2; i < displayedUnlockButtons.Count; ++i) {
                    displayedUnlockButtons[i].transform.localPosition = new Vector3((i-  displayedUnlockButtons.Count/2) * unlockButtonSize + unlockButtonSize  /2, - 550,0);
                }
            } else {
                for (int i = 0; i < displayedUnlockButtons.Count; ++i) {
                    displayedUnlockButtons[i].transform.localPosition = new Vector3(-unlockButtonSize * (displayedUnlockButtons.Count / 2) + i * unlockButtonSize, - 550,0);
                }
            }
            if (tree.unlocking != null) {
                currentUnlock.GetComponent<TalentDisplay>().setTalent(tree.unlocking);
                currentUnlock.SetActive(true);
            } else {
                currentUnlock.SetActive(false);
            }

            // Group
            var relevantGroupUnlocks = tree.groupUnlockOptions.Where(t => t.activityId == activityId).ToList();
            
            for (int i = 0; i < relevantGroupUnlocks.Count; ++i) {
                if (i >= displayedGroupUnlockButtons.Count) {
                    displayedGroupUnlockButtons.Add(Instantiate(UnlockButtonPrefab));
                }
                displayedGroupUnlockButtons[i].transform.SetParent(canvas.transform);
                displayedGroupUnlockButtons[i].transform.SetAsLastSibling();
                var displayButton = displayedGroupUnlockButtons[i].GetComponent<TalentDisplay>();
                displayButton.loadTalentTree = this;
                displayButton.tooltip = tooltip;
                displayButton.setTalent(relevantGroupUnlocks[i]);
            }
            var numberOfGroupButtons = displayedGroupUnlockButtons.Count;
            for (int i = relevantGroupUnlocks.Count; i < numberOfGroupButtons; ++i) {
                displayedGroupUnlockButtons[displayedGroupUnlockButtons.Count - 1].Destroy();
                displayedGroupUnlockButtons.RemoveAt(displayedGroupUnlockButtons.Count - 1);
            }
            
            if (displayedGroupUnlockButtons.Count % 2 == 0) {
                for (int i = 0; i < displayedGroupUnlockButtons.Count / 2; ++i) {
                    displayedGroupUnlockButtons[i].transform.localPosition = new Vector3(- unlockButtonSize * (displayedGroupUnlockButtons.Count / 2 - 1) + i * unlockButtonSize - unlockButtonSize  /2, - 750,0);
                }
                for (int i = displayedGroupUnlockButtons.Count / 2; i < displayedGroupUnlockButtons.Count; ++i) {
                    displayedGroupUnlockButtons[i].transform.localPosition = new Vector3((i-  displayedGroupUnlockButtons.Count/2) * unlockButtonSize + unlockButtonSize  /2, - 750,0);
                }
            } else {
                for (int i = 0; i < displayedGroupUnlockButtons.Count; ++i) {
                    displayedGroupUnlockButtons[i].transform.localPosition = new Vector3(-unlockButtonSize * (displayedGroupUnlockButtons.Count / 2) + i * unlockButtonSize, - 750,0);
                }
            }
            if (tree.groupUnlocking != null) {
                currentUnlockGroup.GetComponent<TalentDisplay>().setTalent(tree.groupUnlocking);
                currentUnlockGroup.SetActive(true);
            } else {
                currentUnlockGroup.SetActive(false);
            }
            

            
        }
    }
}
