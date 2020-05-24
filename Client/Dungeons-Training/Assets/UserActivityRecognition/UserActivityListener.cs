using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.Events;

namespace KKUserActivityRecognition {
	public class UserActivityListener : MonoBehaviour {

		[System.Serializable]
		public class UserActivityCallback: UnityEvent<UserActivityInfo> {};

		public UserActivityCallback onUserActivityRecognized = new UserActivityCallback();

		public void OnActivityRecognized(string param) {
			UserActivityInfo parsed = UserActivityInfoParser.parse(param);
			if (parsed != null) {
				onUserActivityRecognized.Invoke(parsed);
			}
		}


		internal class UserActivityInfoParser {
			private static int expectedBodyParts = 7;

			internal static UserActivityInfo parse(string param) {
				if (param.Length < UserActivityInfoParser.expectedBodyParts) {
					return null;
				}

				char[] characters = param.ToCharArray();

				ConfidenceLevel confidenceLevel = ConfidenceLevel.low;
				Dictionary<ActivityType, bool> states = new Dictionary<ActivityType, bool>();
				for (int i = 0; i < expectedBodyParts; i += 1)
				{
					double numericValue = char.GetNumericValue(characters[i]);
					bool isConfidenceLevel = i == expectedBodyParts - 1;
					if (isConfidenceLevel) {
						confidenceLevel = (ConfidenceLevel)numericValue;
					} else {
						bool boolValue = numericValue == 1;
						switch (i) {
							case 0: 
							states.Add(ActivityType.automative, boolValue);
							break;
							case 1:
							states.Add(ActivityType.cycling, boolValue);
							break;
							case 2:
							states.Add(ActivityType.running, boolValue);
							break;
							case 3:
							states.Add(ActivityType.stationary, boolValue);
							break;
							case 4:
							states.Add(ActivityType.unknown, boolValue);
							break;
							case 5: 
							states.Add(ActivityType.walking, boolValue);
							break;
							default: continue;
						}
					}
				}

				DateTime timestamp = DateTime.Now;
				
				try {
					string elapsedTimePart = param.Substring(expectedBodyParts);
					timestamp = timestamp.AddSeconds(double.Parse(elapsedTimePart));
				} catch (Exception e) {
					Debug.Log("could not parse elapsed activity recognition time. " + e.Message);
				}

				return new UserActivityInfo(states, confidenceLevel, timestamp);
			}
		}
	}
}

