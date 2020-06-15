using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.UIElements;
public class DeaktivierePAnel : MonoBehaviour
{
    public GameObject panel;
    public void disablePanel(GameObject p)
    {
        if (p != null)
        {
            p.SetActive(false);
        }
    }
}
