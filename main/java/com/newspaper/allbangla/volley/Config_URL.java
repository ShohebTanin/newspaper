package com.newspaper.allbangla.volley;

//This class is for storing all URLs as a model of URLs

public class Config_URL
{
	private static String base_URL = "http://appsarena.net/dailynews/";		//Default configuration for WAMP - 80 is default port for WAMP and 10.0.2.2 is localhost IP in Android Emulator
	// Server user login url
//	public static String URL_LOGIN = base_URL+"android_login_api/tutors";

	// Server user register url
	public static String URL_DATA_LOADER = base_URL+"newspaper/loaddata.php";
//	public static String URL_NOTIFICATION = base_URL+"android_login_api/tutors/nitification_sql.php";
//	public static String URL_IMAGESAVE = base_URL+"android_login_api/tutors/imagesaveserver.php";
//	public static String URL_MESSAGE_LOADER = base_URL+"android_login_api/tutors/message/loadmessagedata.php";
//	public static String URL_ARTICLE_LOADER = base_URL+"android_login_api/tutors/article_write.php";
//	public static String URL_VERIFY_PHONE = base_URL+"android_login_api/tutors/phoneverify.php";
//	public static String URL_MAP_TASKER = base_URL+"android_login_api/tutors/googlemapdata.php";


//	public static final String TOPIC_GLOBAL = "global";

	// broadcast receiver intent filters
//	public static final String REGISTRATION_COMPLETE = "registrationComplete";
//	public static final String PUSH_NOTIFICATION = "pushNotification";

	// id to handle the notification in the notification tray
//	public static final int NOTIFICATION_ID = 100;
//	public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

//	public static final String SHARED_PREF = "ah_firebase";
	//loaddatatable.php
/*
	private static final String URL_JSON_OBJECT = "http://api.androidhive.info/volley/person_object.json";
	private static final String URL_JSON_ARRAY = "http://api.androidhive.info/volley/person_array.json";
	private static final String URL_STRING_REQ = "http://api.androidhive.info/volley/string_response.html";
	private static final String URL_IMAGE = "http://api.androidhive.info/volley/volley-image.jpg";

	//If you need any parameter passed with the URL (GET) - U need to modify this functions
	public static String get_JSON_Object_URL()
	{
		return URL_JSON_OBJECT;
	}

	public static String get_JSON_Array_URL()
	{
		return URL_JSON_ARRAY;
	}

	public static String get_String_URL(String Input)
	{
		if(Input.length()>0) {
			return Input;
		}
		return URL_STRING_REQ;
	}

	public static String get_Image_URL()
	{
		return URL_IMAGE;
	}
	*/
}
