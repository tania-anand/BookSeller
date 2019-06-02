package com.example.hp.bookseller.presenter;

import android.content.Context;

import com.android.volley.Request;
import com.example.hp.bookseller.contract.UserBookDisplayContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.example.hp.bookseller.utils.ParseResponse;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class UserBookDisplayPresenter {

    private Context context;
    private UserBookDisplayContract.viewer viewer;

    public UserBookDisplayPresenter(Context context, UserBookDisplayContract.viewer viewer) {
        this.context = context;
        this.viewer = viewer;
    }


    public void retreiveBookList() {

        JSONObject object = new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_USERNAME, SharedPreferencesUtil.getInstance(context).getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST, BookSellerUtil.URL_RETRIEVE_USERITEM, object, new MyResponseListener() {
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


    public void deleteBook(final BookBean book){
        JSONObject object = new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_ITEM_ID,String.valueOf(book.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST,BookSellerUtil.URL_DELETE_ITEM, object, new MyResponseListener() {
            @Override
            public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                viewer.onDeleteItemSuccess(book);
            }

            @Override
            public void onMyResponseFailure(String message) {
                viewer.onDeleteItemFailure(message);

            }
        });

    }
}
