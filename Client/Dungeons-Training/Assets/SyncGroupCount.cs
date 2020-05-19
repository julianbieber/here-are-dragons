﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class SyncGroupCount : MonoBehaviour
{
    public Text self;
    private int nextUpdate=1;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    async void Update()
    {
        if(Time.time>=nextUpdate)
        {
            nextUpdate=Mathf.FloorToInt(Time.time)+1;
            var members = await GroupAPI.getGroup();
            self.text = members.Count.ToString();
        }
        
    }
}
