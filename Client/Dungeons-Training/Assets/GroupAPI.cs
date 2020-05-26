﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class GroupAPI
{
     public static async Task<List<int>> getGroup() {
        var response = await API.get<GroupResponse>(Global.baseUrl + "group", new Dictionary<string, string>());
        if (response.isSome) {
            return response.value.users;
        } else {
            Debug.Log("Failed to retrieve group");
            return new List<int>();
        }
    }

    public static async void joinGroup(string otherUser) {
        var response = await API.post<JoinRequest, string>(Global.baseUrl + "joinGroup", new JoinRequest{userName = otherUser}, new Dictionary<string, string>());
        if (response.isSome) {
            return;
        } else {
            Debug.Log("Failed to join group");
            return;
        }
    }

    public static async void leaveGroup() {
        var response = await API.post<string, string>(Global.baseUrl + "leaveGroup", "", new Dictionary<string, string>());
        if (response.isSome) {
            return;
        } else {
            Debug.Log("Failed to join group");
            return;
        }
    }
}

[Serializable]
public class GroupResponse {
    public List<int> users;
}

[Serializable]
class JoinRequest {
    public string userName;
}
