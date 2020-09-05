using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Mapbox.Unity.Location;
using Mapbox.Utils;
public class turnArrowToQuest : MonoBehaviour
{
    public GameObject player;
    public GameObject arrow;
    private int nextUpdate=1;


    // Start is called before the first frame update
    void Start()
    {
    }

    // Update is called once per frame
    void Update()
    {
        if(true&&Time.time>=nextUpdate){
            nextUpdate=Mathf.FloorToInt(Time.time)+1;
            if(Global.ausgewahlterQuest.isSome){
                var map = LocationProviderFactory.Instance.mapManager;
                var coordQuest = map.GeoToWorldPosition(new Vector2d(Global.ausgewahlterQuest.value.latitude, Global.ausgewahlterQuest.value.longitude));
                var coordPlayer = player.GetComponent<Transform>().position;
                Vector3 direction = (coordQuest-coordPlayer);
                var lengthofDir =direction.magnitude;
                direction = direction/lengthofDir;
                if(direction.z>0){
                    arrow.transform.eulerAngles=new Vector3(0,0,(Mathf.Acos(direction.x)/(Mathf.PI)) * 180f-90);
                }else{
                    arrow.transform.eulerAngles=new Vector3(0,0,360 - ((Mathf.Acos(direction.x)/(Mathf.PI)) * 180f)-90);
                }
            }
            else{
                arrow.transform.eulerAngles=new Vector3(0,0,0);
            }
        }
    }
}
