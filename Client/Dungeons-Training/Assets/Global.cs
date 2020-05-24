using System.Collections;
using System.Collections.Generic;


public static class Global 
{
    public static Option<int> userId = Option<int>.None;

    public static Option<string> token = Option<string>.None;

    //public static string baseUrl = "http://127.0.0.1:8888/";
    public static string baseUrl = "http://192.168.0.66:8888/";
}
