package com.example.hp.bookseller.presenter;

import android.content.Context;

import com.android.volley.Request;
import com.example.hp.bookseller.contract.MainContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.example.hp.bookseller.utils.ParseResponse;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MainPresenter {

    private Context context;
    private MainContract.viewer viewer;

    public MainPresenter(Context context ,MainContract.viewer viewer){
        this.context = context;
        this.viewer = viewer;
    }


    public void retreiveBookList(){

        JSONObject object = new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_EMAIL, SharedPreferencesUtil.getInstance(context).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST, BookSellerUtil.URL_RETRIEVE_ALLITEMS, object,new MyResponseListener() {
            @Override
            public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                 viewer.onFetchBookListSuccess(new ParseResponse().getBooksList(jsonObject));
            }

            @Override
            public void onMyResponseFailure(String message) {
                viewer.onFetchBookListFailure(message);

            }
        });


    }

}
