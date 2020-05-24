using UnityEngine;
using System;
using System.Collections;
using System.Runtime.InteropServices;
using UnityEngine.Events;
using System.Text;
using System.Collections.Generic;

namespace KKUserActivityRecognition {

	public enum ConfidenceLevel {
		low = 1, medium = 2, high = 3
	}

	public enum AuthorizationStatus {
		notDetermined = 0,
    	restricted = 1,
    	denied = 2,
    	authorized = 3
	}

	public enum ActivityType {
		automative,
		cycling,
		running,
		stationary,
		unknown,
		walking
	}

	public class UserActivityInfo {
        public ConfidenceLevel confidenceLevel;
		private Dictionary<ActivityType, bool> stateMap;
		public DateTime timestamp;

		public UserActivityInfo( Dictionary<ActivityType, bool> states, ConfidenceLevel confidenceLevel, DateTime timestamp) {
			this.stateMap = states;
			this.confidenceLevel = confidenceLevel;
			this.timestamp = timestamp;
		}

		public override string ToString() {
			StringBuilder builder = new StringBuilder();
			builder.AppendLine("Recognized state:");
			if (IsEmpty) {
				builder.AppendLine("none");
			} else {
				foreach(KeyValuePair<ActivityType, bool> entry in stateMap) {
					if (entry.Value) {
						builder.AppendLine(entry.Key.ToString());
					}
				}
			}

			builder.AppendLine("Confidence: " + confidenceLevel.ToString());
			builder.AppendLine("Recognition time: " + timestamp.ToString());
			return builder.ToString();
		}

		public ActivityType recognizedType {
			get {
				 foreach (KeyValuePair<ActivityType, bool> pair in stateMap) {
					 if (pair.Value)
					 	return pair.Key;
				 }

				 return ActivityType.unknown;
			}
		}

		public bool HasRecognized(ActivityType type) {
			return stateMap[type];
		}

		public bool IsEmpty {
			get {
				return !stateMap.ContainsValue(true);
			}
		}
	}

	public interface RecognitionEngine {
		bool IsAvailable();
		void StartUpdates();
		void StopUpdates();

		int GetAuthorizationStatus();
	}

	/*
	 * check readme.pdf before using!
	 */
	public class UserActivityRecognition : System.Object {

		#pragma warning disable 0414
		private static RecognitionEngine iOSEngine = new AppleEngine();
		private static RecognitionEngine androidEngine = new AndroidEngine();
		private static RecognitionEngine editorEngine = new EditorEngine();
		#pragma warning restore 0414

		private static RecognitionEngine engine {
			get {
			#if UNITY_IOS && !UNITY_EDITOR
				return iOSEngine;
			#elif UNITY_ANDROID && !UNITY_EDITOR
				return androidEngine;
			#else
				return editorEngine;
			#endif
			}

		}
		public static bool IsAvailable() {
			return engine.IsAvailable();
		}

		public static void StartUpdates() {
			engine.StartUpdates();
		}

		public static void StopUpdates() {
			engine.StopUpdates();
		}

		public static AuthorizationStatus AuthorizationStatus() {
			return (AuthorizationStatus)engine.GetAuthorizationStatus();
		}

		private class AppleEngine: RecognitionEngine {
			[DllImport ("__Internal")]
			internal static extern int _AuthorizationStatus();

			[DllImport ("__Internal")]
			internal static extern bool _IsAvailable();

			[DllImport ("__Internal")]
			internal static extern bool _StartUpdates();

			[DllImport ("__Internal")]
			internal static extern bool _StopUpdates();

            public int GetAuthorizationStatus()
            {
                return _AuthorizationStatus();
            }

            public bool IsAvailable()
            {
                return _IsAvailable();
            }

			public void StartUpdates() {
				_StartUpdates();
			}

			public void StopUpdates() {
				_StopUpdates();
			}
        }

		private class AndroidEngine: RecognitionEngine {
			private static AndroidJavaObject bridge {
				get {
					return new AndroidJavaClass("com.kokosoft.activityrecognition.client.UnityActivityRecognitionBridge");
				}
			}

            public int GetAuthorizationStatus()
            {
                return bridge.CallStatic<int>("authorizationStatus");
            }

            public bool IsAvailable() {
                return bridge.CallStatic<bool>("isAvailable");
            }

			public void StartUpdates() {
				bridge.CallStatic("startUpdates");
			}

			public void StopUpdates() {
				bridge.CallStatic("stopUpdates");
			}
		}

		private class EditorEngine: RecognitionEngine {
            public int GetAuthorizationStatus() {
                return 0;
            }

            public bool IsAvailable() {
				return false;
			}

			public void StartUpdates() {

			}

			public void StopUpdates() {
				
			}
		}
	}

}



