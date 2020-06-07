using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class DungeonUnit : MonoBehaviour
{
    public GameObject onFirePrefab;
    public GameObject targettablePrefab;

    private GameObject onFireObject;
    private GameObject targettableObject;
    // Start is called before the first frame update
    void Start()
    {
        onFireObject = Instantiate(onFirePrefab);
        setNotOnFire();
        targettableObject = Instantiate(targettablePrefab);
        setNotTargettable();
    }

    // Update is called once per frame
    void Update()
    {
        onFireObject.transform.position = transform.position;
        onFireObject.transform.localScale = transform.localScale;

        targettableObject.transform.position = transform.position;
        targettableObject.transform.localScale = transform.localScale;

    }

    public void setOnFire() {
        onFireObject.SetActive(true);
    }

    public void setTargettable() {
        targettableObject.SetActive(true);
    }
    
    public void setNotOnFire() {
        onFireObject.SetActive(false);
    }

    public void setNotTargettable() {
        targettableObject.SetActive(false);
    }
}
