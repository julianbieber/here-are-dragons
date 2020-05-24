using UnityEngine;
using UnityEditor;
using UnityEditor.Callbacks;
using System.Collections;
#if UNITY_IOS
using UnityEditor.iOS.Xcode;
#endif
using System.IO;

namespace KKUserActivityRecognition {
		public class SetSpeechRecognitionPermissionsOniOS {

		public static bool shouldRun = true;
		/* check readme.pdf for explanation on those keys */
		public static string motionUsageDescription = "Recognizing user activity.";

		private static string nameOfPlist = "Info.plist";
		private static string keyForMotionUsage = "NSMotionUsageDescription";

		#if UNITY_IOS
		[PostProcessBuild]
		public static void ChangeXcodePlist(BuildTarget buildTarget, string pathToBuiltProject) {

			if (shouldRun && buildTarget == BuildTarget.iOS) {

				// Get plist
				string plistPath = pathToBuiltProject + "/" + nameOfPlist;
				PlistDocument plist = new PlistDocument();
				plist.ReadFromString(File.ReadAllText(plistPath));

				// Get root
				PlistElementDict rootDict = plist.root;

				rootDict.SetString(keyForMotionUsage, motionUsageDescription);

				// Write to file
				File.WriteAllText(plistPath, plist.WriteToString());
			}
		}
		#endif

	}
}

