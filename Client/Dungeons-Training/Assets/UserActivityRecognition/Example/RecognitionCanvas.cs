using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using KKUserActivityRecognition;

public class RecognitionCanvas : MonoBehaviour {

	private string StartRecognitionText = "Start Recognition";
	private string StopRecognitionText = "Stop Recognition";
	private string RecognitionNotAvailableText = "Recognition Not Available :-(";

	public Text canvasText;
	public Button button;

	// Use this for initialization
	void Start () {
		Debug.Log("Start...");
		bool isAvailable = UserActivityRecognition.IsAvailable();

		canvasText.text = "Is available: " + isAvailable.ToString() + ", authorizationStatus: " + UserActivityRecognition.AuthorizationStatus();

		if (isAvailable) {
			SetButtonText(StartRecognitionText);
			button.onClick.AddListener(StartRecognition);
			// make sure your listener prefab exists on current scene!
			GameObject.FindObjectOfType<UserActivityListener>()
				.onUserActivityRecognized
				.AddListener(OnActivityRecognized);
		} else {
			button.enabled = false;
			SetButtonText(RecognitionNotAvailableText);
		}
	}

	private void SetButtonText(string text) {
		button.GetComponentInChildren<Text>().text = text;
	}

	void StartRecognition() {
		Debug.Log("Start Recognition");
		button.onClick.RemoveListener(StartRecognition);
		button.onClick.AddListener(StopRecognition);
		SetButtonText(StopRecognitionText);

		UserActivityRecognition.StartUpdates();
	}

	void StopRecognition() {
		button.onClick.RemoveListener(StopRecognition);
		button.onClick.AddListener(StartRecognition);
		SetButtonText(StartRecognitionText);

		UserActivityRecognition.StopUpdates();
	}

	void OnActivityRecognized(UserActivityInfo info) {
		Debug.Log("OnActivityRecognized " + info.ToString());
		canvasText.text = info.ToString();
	}
}
