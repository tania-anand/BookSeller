package com.example.hp.bookseller.interfaces;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tania on 07-Jan-16.
 */
public interface MyResponseListener {
    void onMyResponseSuccess(boolean success, JSONObject jsonObject);
    void onMyResponseFailure(String message);
}
