package com.example.hp.bookseller.contract;

import com.example.hp.bookseller.model.ChatBean;

public interface ChatContract {

    interface viewer{
    void onGetMessagesSuccess(ChatBean chat);
    void setClearSendTextField();
    }

    interface presenter{
    }
}
