package com.example.hp.bookseller.utils;

/**
 * Created by HP on 09-08-2017.
 */

public class BookSellerUtil {

       // for user table in hostinger
       public static final String KEY_ID = "_ID";
       public static final String KEY_NAME = "NAME";
       public static final String KEY_PHONE = "PHONE";
       public static final String KEY_EMAIL = "EMAIL";
       public static final String KEY_USERNAME= "USERNAME";
       public static final String KEY_PASSWORD = "PASSWORD";
       public static final String KEY_COLLEGE = "COLLEGE_NAME";
       public static final String KEY_TRAIT = "TRAIT";
       public static final String KEY_TOKEN="TOKEN";
       public static final String KEY_UID="UID";

       // for book table in hostinger
       public static final String KEY_ITEM_ID="_ID";
       public static final String KEY_ITEM_IMAGE="ITEM_IMAGE";
       public static final String KEY_ITEM_NAME="ITEM_NAME";
       public static final String KEY_ITEM_AUTHOR="ITEM_AUTHOR";
       public static final String KEY_ITEM_PUBLISHER="ITEM_PUBLISHER";
       public static final String KEY_ITEM_CONDITION="ITEM_CONDITION";
       public static final String KEY_ITEM_PRICE="ITEM_PRICE";
       public static final String KEY_ITEM_TRAIT="ITEM_TRAIT";

       //for table in JSON format stored in Firebase
       public static final String JSON_CHAT="CHATS";
       public static final String JSON_USER="USERS";

       // for firebase notification
       public static final String ARG_FIREBASE_TOKEN = "FIREBASE_TOKEN";
       public static final String ARG_RECEIVER = "RECIVER";
       public static final String ARG_RECEIVER_UID = "RECIVER_UID";



       // for shared prefrences file
       public static final String SHAREDPREFS_LOGINFLAG="LOGINFLAG";
       public static final String SHAREDPREFS_KEYEMAIL ="KEY_EMAIL";
       public  static final String SHAREDPREFS_FILENAME="preferences_login";
       public  static final String SHAREDPREFS_SENDERTOKEN="SENDER_TOKEN";
       public  static final String SHAREDPREFS_SENDERPASSWORD="SENDER_PASSWORD";
       public  static final String SHAREDPREFS_USERDETAILS="USER_DETAILS";


       public static final String URL = "https://taniaanand31.000webhostapp.com/bookdig/";


       // links to all our URL's
       public static final String URL_INSERT=URL+"insert.php";
       public static final String URL_LOGINCHECK=URL+"loginQuery.php";
       public static final String URL_RETRIEVE=URL+"retrieveRegistration.php";
       public static final String URL_UPDATE=URL+"update.php";
       public static final String URL_ITEMINSERT=URL+"insertitem.php";
       public static final String URL_DELETE_ITEM=URL+"deleteUserItem.php";
       public static final String URL_RETRIEVE_ITEM=URL+"retrieveItem.php";
       public static final String URL_RETRIEVE_USERITEM=URL+"retrieveUserItems.php";
       public static final String URL_RETRIEVE_ALLITEMS=URL+"retrieveALL.php";
       public static final String URL_UPDATE_ITEM=URL+"updateItem.php";
       public static final String URL_RETRIEVE_PHONENO=URL+"rPhoneNo.php";


}
