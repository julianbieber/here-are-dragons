using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ErkennenObQuestInNäheVonPlayer : MonoBehaviour{ 
	public Sprite far;
	public Sprite near;
	public Sprite supernear;

  private void OnTriggerEnter(Collider other){
	Debug.Log(other.ToString());
	GameObject change = other.gameObject;
	if(change.GetComponent<SphereCollider>().radius == 50){
		change.GetComponent<SphereCollider>().radius = 30;
		change.GetComponent<SpriteRenderer>().sprite =near;
	}
	if(change.GetComponent<SphereCollider>().radius == 30){
		change.GetComponent<SphereCollider>().radius = 3;
		change.GetComponent<SpriteRenderer>().sprite =supernear;
	}
  } 
  private void OnTriggerExit(Collider other){
	GameObject change = other.gameObject;
	if( change.GetComponent<SphereCollider>().radius == 30){
		change.GetComponent<SphereCollider>().radius = 50;
		change.GetComponent<SpriteRenderer>().sprite =far;
	}
  }
}
