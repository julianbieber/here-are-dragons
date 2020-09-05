using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mapbox.Unity.Location;
using Mapbox.Utils;
using Mapbox.Map;
using Mapbox.Unity.Utilities;

public class Quest
{
    public Option<DAOQuest> ausgewählterQuest;
    private GameObject Player;

    /*
    Die Methode gibt einen Boolean aus, der angibt, ob der Player in Nähe des Quests ist oder nicht.
    */
    public bool istPlayerAnPositionVonAusgewahltemQuest()
    {
        var map = LocationProviderFactory.Instance.mapManager;
        var positionSpieler = Player.transform.position;
        if (ausgewählterQuest.isSome)
        {
            var questPosition = map.GeoToWorldPosition(new Vector2d(ausgewählterQuest.value.latitude, ausgewählterQuest.value.longitude));
            if (GetDistanceBetweenPoints(positionSpieler, questPosition) < 5f)
            {
                return true;
            }
        }
        return false;
    }
    public bool anulus()
    {
        var map = LocationProviderFactory.Instance.mapManager;
        var positionSpieler = Player.transform.position;
        if (ausgewählterQuest.isSome)
        {
            var questPosition = map.GeoToWorldPosition(new Vector2d(ausgewählterQuest.value.latitude, ausgewählterQuest.value.longitude));
            if (GetDistanceBetweenPoints(positionSpieler, questPosition) > 20.0f)
            {
                return true;
            }
        }
        return false;
    }
    public double getDistanceToPlayer(){
        var map = LocationProviderFactory.Instance.mapManager;

        var positionSpieler = map.WorldToGeoPosition(Player.transform.position);
        var questPosition = new Vector2d(ausgewählterQuest.value.latitude,ausgewählterQuest.value.longitude);

        var distanceVector = (positionSpieler-questPosition);
        return Conversions.LatLonToMeters(distanceVector.x,distanceVector.y).magnitude;
    }


    public float GetDistanceBetweenPoints(Vector3 point1, Vector3 point2)
    {
        var a = (point2.x - point1.x);
        var b = (point2.y - point1.y);
        var c = (point2.z - point1.z);

        return Mathf.Sqrt(Mathf.Pow(a, 2) + Mathf.Pow(b, 2) + Mathf.Pow(c, 2));      // c^2 = a^2 + b^2 -> c = Sqrt(a^2 + b^2)
    }
    public Quest(Option<DAOQuest> a, GameObject P)
    {
        ausgewählterQuest = a;
        Player = P;
    }

}

