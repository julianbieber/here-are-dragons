using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using Mapbox.Unity.Location;

public class SendPosition : MonoBehaviour
{
    public ILocationProvider fp;
    private int nextUpdate = 1;

    // Start is called before the first frame update
    void Start()
    {
        fp = LocationProviderFactory.Instance.DefaultLocationProvider;
    }

    // Update is called once per frame
    void Update()
    {
        if (Time.time >= nextUpdate)
        {
            nextUpdate = Mathf.FloorToInt(Time.time) + 1;
            var pos = fp.CurrentLocation.LatitudeLongitude;
            //PositionAPI.setPosition((float)pos.y, (float)pos.x);
        }
    }
    public async void Create()
    {
        var pos = fp.CurrentLocation.LatitudeLongitude;
        PositionAPI.setPosition((float)pos.y, (float)pos.x);
    }
}
