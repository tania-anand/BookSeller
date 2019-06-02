package com.example.hp.bookseller.contract;

public interface LoginContract {


    interface view {
        void loginClickSuccess();
        void loginClickFailure(String message);
    }

    interface presenter{
    }

}
