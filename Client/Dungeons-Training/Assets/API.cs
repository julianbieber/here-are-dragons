using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine;
using System.Text;
using System.Linq;

public class API {

    public static async Task<Option<B>> get<B>(string url, Dictionary<string, string> queryParemeters) {
        var urlWithQuery = url + queryString(queryParemeters);
        Debug.Log(Global.userId.value);
        using (var www = UnityWebRequest.Get(urlWithQuery))
            {
                www.SetRequestHeader("Content-Type", "application/json");
                www.SetRequestHeader("Accept", "application/json");

                if (Global.token.isSome && Global.userId.isSome) {
                    www.SetRequestHeader("X-userId", Global.userId.value.ToString());
                    www.SetRequestHeader("X-token", Global.token.value);
                }
                www.SendWebRequest();
                
                while (!www.isDone)
                    await Task.Delay(1);
                var responseString = www.downloadHandler.text;
                if (www.isNetworkError || www.isHttpError) {
                    return Option<B>.None;
                } else {
                    return Option<B>.Some(JsonUtility.FromJson<B>(responseString));
                }
            }
    }

    public static async Task<Option<B>> post<A, B>(string url, A data, Dictionary<string, string> queryParemeters) {
        var requestJson = JsonUtility.ToJson(data);
        byte[] bodyRaw = Encoding.UTF8.GetBytes(requestJson);
        var urlWithQuery = url + queryString(queryParemeters);
        using (var www = UnityWebRequest.Post(urlWithQuery, requestJson))
            {
                www.uploadHandler = (UploadHandler) new UploadHandlerRaw(bodyRaw);
                www.SetRequestHeader("Content-Type", "application/json");
                www.SetRequestHeader("Accept", "application/json");
                if (Global.token.isSome && Global.userId.isSome) {
                    www.SetRequestHeader("X-userId", Global.userId.value.ToString());
                    www.SetRequestHeader("X-token", Global.token.value);
                }
                www.SendWebRequest();
                
                while (!www.isDone)
                    await Task.Delay(1);
                var responseString = www.downloadHandler.text;
                if (www.isNetworkError || www.isHttpError) {
                    return Option<B>.None;
                } else {
                    return Option<B>.Some(JsonUtility.FromJson<B>(responseString));
                }
            }
    }

    public static async Task<Option<B>> put<A, B>(string url, A data, Dictionary<string, string> queryParemeters) {
        var requestJson = JsonUtility.ToJson(data);
        byte[] bodyRaw = Encoding.UTF8.GetBytes(requestJson);
        var urlWithQuery = url + queryString(queryParemeters);
        using (var www = UnityWebRequest.Put(urlWithQuery, requestJson))
            {
                www.uploadHandler = (UploadHandler) new UploadHandlerRaw(bodyRaw);
                www.SetRequestHeader("Content-Type", "application/json");
                www.SetRequestHeader("Accept", "application/json");
                if (Global.token.isSome && Global.userId.isSome) {
                    www.SetRequestHeader("X-userId", Global.userId.value.ToString());
                    www.SetRequestHeader("X-token", Global.token.value);
                }
                www.SendWebRequest();
                
                while (!www.isDone)
                    await Task.Delay(1);
                var responseString = www.downloadHandler.text;
                if (www.isNetworkError || www.isHttpError) {
                    return Option<B>.None;
                } else {
                    return Option<B>.Some(JsonUtility.FromJson<B>(responseString));
                }
            }
    }
    public static async Task<Option<B>> delete<B>(string url, Dictionary<string, string> queryParemeters) {
        var urlWithQuery = url + queryString(queryParemeters);
        using (var www = UnityWebRequest.Delete(urlWithQuery))
            {
                www.SetRequestHeader("Content-Type", "application/json");
                www.SetRequestHeader("Accept", "application/json");
                if (Global.token.isSome && Global.userId.isSome) {
                    www.SetRequestHeader("X-userId", Global.userId.value.ToString());
                    www.SetRequestHeader("X-token", Global.token.value);
                }
                www.SendWebRequest();
                
                while (!www.isDone)
                    await Task.Delay(1);
                var responseString = www.downloadHandler.text;
                if (www.isNetworkError || www.isHttpError) {
                    return Option<B>.None;
                } else {
                    return Option<B>.Some(JsonUtility.FromJson<B>(responseString));
                }
            }
    }


    static string queryString(Dictionary<string, string> queryParemeters) {
        StringBuilder queryBuilder = new StringBuilder();
        if (queryParemeters.Count > 0) {
            queryBuilder.Append("?");
        }
        
        foreach (var parameter in queryParemeters)
        {
            var key = parameter.Key;
            var value = parameter.Value;
            queryBuilder.Append(key);
            queryBuilder.Append("=");
            queryBuilder.Append(UnityWebRequest.EscapeURL(value));
            queryBuilder.Append("&");
        }
        return queryBuilder.ToString();
    }
        
}

