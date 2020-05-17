using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine;

public class AccountAPI {
    static string baseUrl = "http://192.168.0.66:8888/";

    public static async Task<Option<int>> createAccount(string user, string password) {
        var request = new AccountRequestBody{name = user, password = password};
        var response = await API.post<AccountRequestBody, CreateAccountResponseBody>(baseUrl + "createUser", request, new Dictionary<string, string>());
        if (response.isSome) {
            return Option<int>.Some(response.value.id);
        } else {
            return Option<int>.None;
        }
    }

    public static async Task<Option<Tuple<int, string>>> login(string user, string password) {
        var request = new AccountRequestBody{name = user, password = password};
        var response = await API.post<AccountRequestBody, LoginResponseBody>(baseUrl + "login", request, new Dictionary<string, string>());
        if (response.isSome) {
            var tuple = new Tuple<int, string>(response.value.id, response.value.token);
            return Option<Tuple<int, string>>.Some(tuple);
        } else {
            return Option<Tuple<int, string>>.None;
        }
    }
        
}


[Serializable]
public class AccountRequestBody {
    public string name;
    public string password;
}

[Serializable]
public class CreateAccountResponseBody {
    public int id;
}

public class LoginResponseBody {
    public int id;

    public string token;
}