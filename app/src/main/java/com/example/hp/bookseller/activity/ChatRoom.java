package com.example.hp.bookseller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.adapter.ChatUserListAdapter;
import com.example.hp.bookseller.contract.ChatRoomContract;
import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.presenter.ChatRoomPresenter;
import com.example.hp.bookseller.utils.UtilActivity;
import com.example.hp.bookseller.utils.ViewItemClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoom extends UtilActivity implements ChatRoomContract.viewer, MyResponseConnectivity {

    @BindView(R.id.chat_room_recycler)
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatUserListAdapter chatUserListAdapter;

    private ChatRoomPresenter presenter;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void init() {

        initMyResponseConnectivityListener(this);

        presenter = new ChatRoomPresenter(this,this);

        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnItemTouchListener( new ViewItemClick(getApplicationContext(), new ViewItemClick.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                presenter.startChatActivity(position);
            }
        }));

        if(isNetworkConnected(ChatRoom.this)){
            initProgressDialog();
            showProgressDialog(1);
            presenter.retrieveChatList();
            presenter.retrieveDataFromFirebase();
        }else
            showMsg(ChatRoom.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room );
        init();
    }

    @Override
    public void onChatListRetreiveSucess(ArrayList <String> chatList) {
        showProgressDialog(0);
    }

    @Override
    public void onChatListRetreiveFailure() {
        showProgressDialog(0);
    }

    @Override
    public void onDataFromFirebaseSucess(ArrayList<UserBean> userBeanArrayList) {
        chatUserListAdapter = new ChatUserListAdapter(ChatRoom.this, R.layout.chat_room, userBeanArrayList);
        recyclerView.setAdapter(chatUserListAdapter);
        showProgressDialog(0);

    }

    @Override
    public void oneDataFromFirebaseFailure() {
        showProgressDialog(0);
    }

    @Override
    public void onMyResponseConnectivity(int i) {
        if(i==1){
            if(isNetworkConnected(ChatRoom.this)){
                initProgressDialog();
                showProgressDialog(1);
                presenter.retrieveChatList();
                presenter.retrieveDataFromFirebase();
            }else
                showMsg(ChatRoom.this);
        }else{
            showMsg(ChatRoom.this);
        }
    }
}

