package com.example.hp.bookseller.utils;


import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.model.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParseResponse {


    public ArrayList<BookBean> getBooksList(JSONObject jsonObject){
        ArrayList<BookBean> bookitemlist = new ArrayList <>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jObj = jsonArray.getJSONObject(i);
                    BookBean bookBean = new BookBean();
                    bookBean.setId(jObj.getInt(BookSellerUtil.KEY_ITEM_ID));
                    bookBean.setName(jObj.getString(BookSellerUtil.KEY_ITEM_NAME));
                    bookBean.setImage(jObj.getString(BookSellerUtil.KEY_ITEM_IMAGE));
                    bookBean.setPublisher(jObj.getString(BookSellerUtil.KEY_ITEM_PUBLISHER));
                    bookBean.setAuthor(jObj.getString(BookSellerUtil.KEY_ITEM_AUTHOR));
                    bookBean.setPrice(jObj.getString(BookSellerUtil.KEY_ITEM_PRICE));
                    bookBean.setCondition(jObj.getString(BookSellerUtil.KEY_ITEM_CONDITION));
                    bookBean.setUserName(jObj.getString(BookSellerUtil.KEY_USERNAME));
                    bookBean.setTrait(jObj.getString(BookSellerUtil.KEY_ITEM_TRAIT));
                    bookitemlist.add(bookBean);
                } } }
        catch (Exception e){ }
        return  bookitemlist;
    }


    public UserBean getUser(JSONObject jsonObject){
        UserBean user = new UserBean();
        try{
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                user.setName(jObj.getString(BookSellerUtil.KEY_NAME));
                user.setPhone(jObj.getString(BookSellerUtil.KEY_PHONE));
                user.setEmail(jObj.getString(BookSellerUtil.KEY_EMAIL));
                user.setToken(jObj.getString(BookSellerUtil.KEY_TOKEN));
                user.setUid(jObj.getString(BookSellerUtil.KEY_UID));
            } } }
        catch (JSONException e) {
        e.printStackTrace(); }
    return user;
    }
}
