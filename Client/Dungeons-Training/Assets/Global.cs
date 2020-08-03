using System.Collections;
using System.Collections.Generic;


public static class Global 
{
    public static Option<int> userId = Option<int>.Some(1);

    public static Option<DAOQuest> ausgewahlterQuest = Option<DAOQuest>.None;
    public static Option<string> token = Option<string>.Some("DEBUG");
    public static Option<float> TimeOfLogin = Option<float>.None;

    public static Option<int> difficulty = Option<int>.None;



    //public static string baseUrl = "http://127.0.0.1:8888/";
    public static string baseUrl = "http://192.168.0.66:8888/";
    //public static string baseUrl = "http://hesse.guru:8888/";
}
