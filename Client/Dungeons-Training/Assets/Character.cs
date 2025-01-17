﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Character : MonoBehaviour
{

    public Option<PlayerCharacter> player = Option<PlayerCharacter>.None;
    public Skillbar skillbar;

    private int nextUpdate = 0;
    // Start is called before the first frame update
    void Start()
    {
        player = Option<PlayerCharacter>.None;
    }

    // Update is called once per frame
    async void Update()
    {   
        if (Time.time >= nextUpdate) {
            nextUpdate = (int)(Time.time) + 2;

            player = await CharacterAPI.getCharacter();

            if (player.isSome && player.value.skillBar.selected != null && skillbar != null) {
                skillbar.updateCharacter(player.value.skillBar.selected, 100, true);
            }

        }
        
    }
}
