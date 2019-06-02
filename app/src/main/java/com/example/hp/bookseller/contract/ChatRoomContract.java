package com.example.hp.bookseller.contract;

import com.example.hp.bookseller.model.UserBean;

import java.util.ArrayList;

public interface ChatRoomContract {

    interface viewer{
        void onChatListRetreiveSucess(ArrayList<String> chatList );
        void onChatListRetreiveFailure();
        void onDataFromFirebaseSucess(ArrayList<UserBean> userBeanArrayList);
        void oneDataFromFirebaseFailure();
    }

    interface presenter{
    }
}
