using System.IO;
using System.Text;
using System.Xml;
using UnityEditor.Android;

public class ModifyUnityAndroidAppManifestSample : IPostGenerateGradleAndroidProject
{

    public void OnPostGenerateGradleAndroidProject(string basePath)
    {
        // If needed, add condition checks on whether you need to run the modification routine.
        // For example, specific configuration/app options enabled

        var androidManifest = new AndroidManifest(GetManifestPath(basePath));

        androidManifest.UpdateToIncludeActivityRecognitionPermissions();

        // Add your XML manipulation routines

        androidManifest.Save();
    }

    public int callbackOrder { get { return 1; } }

    private string _manifestFilePath;

    private string GetManifestPath(string basePath)
    {
        if (string.IsNullOrEmpty(_manifestFilePath))
        {
            var pathBuilder = new StringBuilder(basePath);
            pathBuilder.Append(Path.DirectorySeparatorChar).Append("src");
            pathBuilder.Append(Path.DirectorySeparatorChar).Append("main");
            pathBuilder.Append(Path.DirectorySeparatorChar).Append("AndroidManifest.xml");
            _manifestFilePath = pathBuilder.ToString();
        }
        return _manifestFilePath;
    }
}


internal class AndroidXmlDocument : XmlDocument
{
    private string m_Path;
    protected XmlNamespaceManager nsMgr;
    public readonly string AndroidXmlNamespace = "http://schemas.android.com/apk/res/android";
    public AndroidXmlDocument(string path)
    {
        m_Path = path;
        using (var reader = new XmlTextReader(m_Path))
        {
            reader.Read();
            Load(reader);
        }
        nsMgr = new XmlNamespaceManager(NameTable);
        nsMgr.AddNamespace("android", AndroidXmlNamespace);
    }

    public string Save()
    {
        return SaveAs(m_Path);
    }

    public string SaveAs(string path)
    {
        using (var writer = new XmlTextWriter(path, new UTF8Encoding(false)))
        {
            writer.Formatting = Formatting.Indented;
            Save(writer);
        }
        return path;
    }
}


internal class AndroidManifest : AndroidXmlDocument
{
    public AndroidManifest(string path) : base(path)
    {
        
    }

    private XmlAttribute CreateAndroidAttribute(string key, string value)
    {
        XmlAttribute attr = CreateAttribute("android", key, AndroidXmlNamespace);
        attr.Value = value;
        return attr;
    }

    internal void UpdateToIncludeActivityRecognitionPermissions()
    {
        var application = SelectSingleNode("/manifest/application");
        XmlAttribute netSecAttribute = CreateAndroidAttribute("networkSecurityConfig", "@xml/network_security_config");
        application.Attributes.Append(netSecAttribute);

        XmlElement child = CreateElement("receiver");
        XmlAttribute newAttribute = CreateAndroidAttribute("name", "com.example.activitytracking.ActivityReceiver");
        child.Attributes.Append(newAttribute);
        application.AppendChild(child);

        XmlElement child2 = CreateElement("receiver");
        XmlAttribute newAttribute2 = CreateAndroidAttribute("name", "com.example.activitytracking.LocationReceiver");
        child2.Attributes.Append(newAttribute2);
        application.AppendChild(child2);

        XmlElement service = CreateElement("service");
        service.Attributes.Append(CreateAndroidAttribute("name", "com.example.activitytracking.SimpleService"));
        service.Attributes.Append(CreateAndroidAttribute("enabled", "true"));
        service.Attributes.Append(CreateAndroidAttribute("exported", "false"));

        application.AppendChild(service);

        var manifest = SelectSingleNode("/manifest");
        
        XmlElement permission = CreateElement("uses-permission");
        XmlAttribute permissionAttribute = CreateAndroidAttribute("name", "android.permission.FOREGROUND_SERVICE");
        permission.Attributes.Append(permissionAttribute);
        manifest.AppendChild(permission);


        XmlElement permission2 = CreateElement("uses-permission");
        XmlAttribute permissionAttribute2 = CreateAndroidAttribute("name", "android.permission.WAKE_LOCK");
        permission2.Attributes.Append(permissionAttribute2);
        manifest.AppendChild(permission2);
        
    }
}