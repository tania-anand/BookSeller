package com.example.hp.bookseller.contract;

public interface InsertBookContract {
    interface viewer{
        void onInsertSuccess();
        void onInsertFailure();
    }

    interface presenter{
    }
}
