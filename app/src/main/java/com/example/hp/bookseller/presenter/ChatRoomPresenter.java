package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.activity.Chat;
import com.example.hp.bookseller.activity.ChatRoom;
import com.example.hp.bookseller.adapter.ChatUserListAdapter;
import com.example.hp.bookseller.contract.ChatRoomContract;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatRoomPresenter {

    private Context context;
    private ChatRoomContract.viewer viewer;

    private ArrayList<UserBean> userArrayList;
    LinearLayoutManager linearLayoutManager;
    ChatUserListAdapter chatUserListAdapter;



    // firebase Database
    private DatabaseReference mUsersTable;
    private DatabaseReference mChatsTable;

    private FirebaseAuth mAuth;

    int  arraylistPosition=0;
    ArrayList<String> chatList;

    public ChatRoomPresenter(Context context ,ChatRoomContract.viewer viewer){
        this.viewer = viewer;
        this.context = context;

        mAuth = FirebaseAuth.getInstance();
    }

    public void retrieveChatList() {
        mUsersTable = FirebaseDatabase.getInstance().getReference().child(BookSellerUtil.JSON_USER);

        mUsersTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    chatList =(ArrayList<String>)dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("chats").getValue();
                    viewer.onChatListRetreiveSucess(chatList);
                }
                catch(Exception e) {
                    viewer.onChatListRetreiveFailure();
                }
                Log.d("HCHATLIST",String.valueOf(chatList));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                viewer.onChatListRetreiveFailure();

            }});
    }


    public void retrieveDataFromFirebase () {
        mChatsTable =FirebaseDatabase.getInstance().getReference().child(BookSellerUtil.JSON_CHAT);

        mUsersTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList= new ArrayList<>();
//            Log.d("Data snapshot",String.valueOf(dataSnapshot));
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        UserBean user = snapshot.getValue(UserBean.class);
                        if (!TextUtils.equals(user.getUid(), String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()))) {
                            Log.d("If 1 ", user.toString());
                            Log.d("User Info", user.toString());
                            if (chatList.contains(user.getEmail())) {
                                Log.d("If 2 ", user.toString());
                                userArrayList.add(user);
                            }
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Exception  ", String.valueOf(e));
                        viewer.oneDataFromFirebaseFailure();
                    }
                }

                viewer.onDataFromFirebaseSucess(userArrayList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                viewer.oneDataFromFirebaseFailure();
            }
        });
    }

    public void startChatActivity(int position){
        UserBean userBean = userArrayList.get(position);
        Intent i = new Intent(context, Chat.class);
        i.putExtra("reciver",userBean);
        Log.i("Reciver Info ",userBean.toString());
        context.startActivity(i);

    }






}
