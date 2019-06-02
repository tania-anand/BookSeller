package com.example.hp.bookseller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.hp.bookseller.model.UserBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static SharedPreferencesUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesUtil();
            String SHARE_USER_INFO = "userinfo";
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.apply();
        }
        return instance;
    }

    public boolean getIsLoggedIn() {
        return preferences.getBoolean(BookSellerUtil.SHAREDPREFS_LOGINFLAG,false);
    }

    public String getEmail() {
        Log.d("email",preferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,""));
        return preferences.getString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,"");
    }

    public String getToken() {
        Log.d("email",preferences.getString(BookSellerUtil.SHAREDPREFS_SENDERTOKEN,""));
        return preferences.getString(BookSellerUtil.SHAREDPREFS_SENDERTOKEN,"");
    }



    public void setLoginDetails(String username ,String password){
        editor.putBoolean(BookSellerUtil.SHAREDPREFS_LOGINFLAG,true);
        editor.putString(BookSellerUtil.SHAREDPREFS_KEYEMAIL,username);
        editor.putString(BookSellerUtil.SHAREDPREFS_SENDERPASSWORD,password);
        editor.putString(BookSellerUtil.SHAREDPREFS_SENDERTOKEN, FirebaseInstanceId.getInstance().getToken());
        editor.commit();
    }

    public void saveUserDetails(UserBean user){
        editor.putString(BookSellerUtil.SHAREDPREFS_USERDETAILS,new Gson().toJson(user));
        editor.commit();

    }

    public UserBean getUserDetails(){
        return new Gson().fromJson(preferences.getString(BookSellerUtil.SHAREDPREFS_USERDETAILS, null), UserBean.class);
    }

    public void onLogout(){
        editor.putBoolean(BookSellerUtil.SHAREDPREFS_LOGINFLAG,false);
        editor.remove(BookSellerUtil.SHAREDPREFS_KEYEMAIL);
        editor.remove(BookSellerUtil.SHAREDPREFS_SENDERPASSWORD);
        editor.remove(BookSellerUtil.SHAREDPREFS_SENDERTOKEN);
        editor.remove(BookSellerUtil.SHAREDPREFS_USERDETAILS);
        editor.apply();
        editor.commit();

        FirebaseAuth.getInstance().signOut();

    }



}
