package com.example.hp.bookseller.contract;

import com.example.hp.bookseller.model.BookBean;

import java.util.ArrayList;

public interface UserBookDisplayContract {

    interface viewer{
        void onFetchBookListSuccess(ArrayList<BookBean> arrayList);
        void onFetchBookListFailure(String message);
        void onDeleteItemSuccess(BookBean bookBean);
        void onDeleteItemFailure(String message);
    }

    interface presenter{

    }
}
