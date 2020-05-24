using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System;

public class PositionAPI : MonoBehaviour
{
   public static async void setPosition(float longitude, float latitude) {
	
       var request = new PositionRequestBody{longitude = longitude, latitude= latitude};
       var response = await API.post<PositionRequestBody, string>(Global.baseUrl + "Position", request, new Dictionary<string, string>());
       if (response.isSome) {
           return;
       } else {
           return;
       }
}
}
[Serializable]
public class PositionRequestBody {
   public float longitude;
   public float latitude;
}