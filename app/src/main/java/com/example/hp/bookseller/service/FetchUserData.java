package com.example.hp.bookseller.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FetchUserData extends IntentService {
    private static final String TAG = "FetchUserData";
    public FetchUserData() {
        super("FetchUserData");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"on handle intent");
        fetchUserDetails();
    }

    private void fetchUserDetails(){
        Log.d(TAG,"fetch user details");
        JSONObject object = new JSONObject();

        try {
            object.put(BookSellerUtil.KEY_USERNAME, SharedPreferencesUtil.getInstance(getApplicationContext()).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall.getMyDB().initRequestQueue(getApplicationContext());
        NetworkCall.getMyDB().processRequest(Request.Method.POST, BookSellerUtil.URL_RETRIEVE, object, new MyResponseListener() {
            @Override
            public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {

                try {
                    Log.d(TAG,"response success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    int responseSuccess = jsonObject.getInt("success");

                    Log.d(TAG,"response success "+jsonArray.toString());
                    if(responseSuccess == 1){
                    int id = 0;
                    String N = "", E = "", P = "", U = "", PW = "", C = "", T = "";

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jObj = jsonArray.getJSONObject(i);

                        id = jObj.getInt(BookSellerUtil.KEY_ID);
                        N = jObj.getString(BookSellerUtil.KEY_NAME);
                        E = SharedPreferencesUtil.getInstance(getApplicationContext()).getEmail();
                        P = jObj.getString(BookSellerUtil.KEY_PHONE);
                        U = jObj.getString(BookSellerUtil.KEY_USERNAME);
                        PW = jObj.getString(BookSellerUtil.KEY_PASSWORD);
                        C = jObj.getString(BookSellerUtil.KEY_COLLEGE);
                        T = jObj.getString(BookSellerUtil.KEY_TRAIT);

                        UserBean user1 = new UserBean(id, N, P, E, U, PW, C, T, "", "");
                        SharedPreferencesUtil.getInstance(getApplicationContext()).saveUserDetails(user1);
                    }
                    }
                }catch(Exception e){
                    Log.d(TAG,"response exception");
                }

            }

            @Override
            public void onMyResponseFailure(String message) {
                Log.d(TAG,"response failure");

            }
        });
    }


}
