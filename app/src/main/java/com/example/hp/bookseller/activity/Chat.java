package com.example.hp.bookseller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.adapter.ChatAdapter;
import com.example.hp.bookseller.contract.ChatContract;
import com.example.hp.bookseller.fcm.FcmNotificationBuilder;
import com.example.hp.bookseller.model.ChatBean;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.presenter.ChatPresenter;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.example.hp.bookseller.utils.UtilActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Chat extends UtilActivity implements TextView.OnEditorActionListener , ChatContract.viewer {
    private static final String TAG = "Chat";

    @BindView(R.id.recycler_view_chat)
    RecyclerView mRecyclerViewChat;
    LinearLayoutManager linearLayoutManager;
    ArrayList<ChatBean> chatBeanArrayList;

    @BindView(R.id.edit_text_message)
    EditText mETxtMessage;
    private ChatAdapter mChatRecyclerAdapter;

    private ChatPresenter presenter;

    private void init() {
        ButterKnife.bind(this);

        mRecyclerViewChat.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerViewChat.setLayoutManager(linearLayoutManager);

        mETxtMessage.setOnEditorActionListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        chatBeanArrayList = new ArrayList<>();
        Intent getIntent = getIntent();
        presenter = new ChatPresenter(this,this);
        presenter.setReciver((UserBean)getIntent.getSerializableExtra("reciver"));
        presenter.getMessageFromFirebaseUser();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // for On Editor Text
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            presenter.sendMessage(mETxtMessage.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onGetMessagesSuccess(ChatBean chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatAdapter(getApplicationContext(), R.layout.chat_room,chatBeanArrayList);
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void setClearSendTextField() {
        mETxtMessage.setText("");
    }
}
