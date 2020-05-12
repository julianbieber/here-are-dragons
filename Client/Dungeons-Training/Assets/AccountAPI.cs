using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine;

public class AccountAPI {
    static string baseUrl = "http://192.168.0.66:8888/";

    public static async Task<Option<int>> createAccount(string user, string password) {
        var request = new CreateAccountRequestBody{name = user, password = password};
        var response = await API.post<CreateAccountRequestBody, CreateAccountResponseBody>(baseUrl + "createUser", request);
        if (response.isSome) {
            return Option<int>.Some(response.value.id);
        } else {
            return Option<int>.None;
        }
        
    }
        
}


[Serializable]
public class CreateAccountRequestBody {
    public string name;
    public string password;
}

[Serializable]
public class CreateAccountResponseBody {
    public int id;
}