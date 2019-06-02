package com.example.hp.bookseller.contract;

import com.example.hp.bookseller.model.BookBean;

import java.util.ArrayList;

public interface MainContract {

    interface viewer{
        void onFetchBookListSuccess(ArrayList<BookBean> arrayList);
        void onFetchBookListFailure(String message);
    }

    interface presenter{
    }
}
