using System.Collections;
using System.Collections.Generic;
using System;
using System.Threading.Tasks;
using UnityEngine.Networking;
using UnityEngine;
using System.Text;

public class API {
    
    public static async Task<Option<B>> post<A, B>(string url, A data) {
        var requestJson = JsonUtility.ToJson(data);
        byte[] bodyRaw = Encoding.UTF8.GetBytes(requestJson);
        Debug.Log(requestJson);
        using (var www = UnityWebRequest.Post(url, requestJson))
            {
                www.uploadHandler = (UploadHandler) new UploadHandlerRaw(bodyRaw);
                www.SetRequestHeader("Content-Type", "application/json");
                www.SetRequestHeader("Accept", "application/json");
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
        
}
