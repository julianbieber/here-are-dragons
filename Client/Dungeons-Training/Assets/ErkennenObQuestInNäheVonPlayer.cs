using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ErkennenObQuestInNäheVonPlayer : MonoBehaviour
{
    public Sprite far;
    public Sprite near;
    public Sprite supernear;


      void Update()
    {
    }
    private void OnTriggerStay(Collider other)
    {
        GameObject change = other.gameObject;
        bool schonBahandelt = change.GetComponent<SchonBehandelterQuest>().schonBehandelt;

        if (change.GetComponent<SphereCollider>().radius == 10&& schonBahandelt  == false)
        {
            change.GetComponent<SphereCollider>().radius = 0.5f;
            change.GetComponent<SpriteRenderer>().sprite = supernear;
            change.GetComponent<SchonBehandelterQuest>().schonBehandelt =true;
        }
        if (change.GetComponent<SphereCollider>().radius == 50 &&schonBahandelt == false  )
        {
            change.GetComponent<SphereCollider>().radius = 10;
            change.GetComponent<SpriteRenderer>().sprite = near;
            change.GetComponent<SchonBehandelterQuest>().schonBehandelt =true;
        }

    }
    private void OnTriggerExit(Collider other)
    {
        
        GameObject change = other.gameObject;
        bool schonBahandelt = change.GetComponent<SchonBehandelterQuest>().schonBehandelt;
        if (change.GetComponent<SphereCollider>().radius == 10&&schonBahandelt == false )
        {
            change.GetComponent<SphereCollider>().radius = 50;
            change.GetComponent<SpriteRenderer>().sprite = far;
            change.GetComponent<SchonBehandelterQuest>().schonBehandelt =true;
        }
        
    }
}
