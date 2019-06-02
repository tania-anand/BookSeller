package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.hp.bookseller.contract.ChatContract;
import com.example.hp.bookseller.fcm.FcmNotificationBuilder;
import com.example.hp.bookseller.model.ChatBean;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatPresenter {

    private static final String TAG = "ChatPresenter";

    private Context context;
    private ChatContract.viewer viewer;

    private FirebaseAuth mAuth;
    private DatabaseReference mChat;
    private DatabaseReference mUser;

    private UserBean sender,reciver;

    public ChatPresenter(Context context, ChatContract.viewer viewer) {
        this.context = context;
        this.viewer = viewer;

        init();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        mChat = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseDatabase.getInstance().getReference();


        sender = new UserBean();
        sender.setEmail(SharedPreferencesUtil.getInstance(context).getEmail());
        sender.setToken(SharedPreferencesUtil.getInstance(context).getToken());
        sender.setUid(mAuth.getCurrentUser().getUid());


    }

    public void setReciver(UserBean reciver) {
        this.reciver = reciver;
    }


    public void getMessageFromFirebaseUser() {
        Toast.makeText(context,"In Method getMessageFromFirebaseUser",Toast.LENGTH_LONG).show();
        final String room_type_1 = sender.getUid() + "_" + reciver.getUid();
        final String room_type_2 = reciver.getUid() + "_" + sender.getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(BookSellerUtil.JSON_CHAT).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(BookSellerUtil.JSON_CHAT)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatBean chat = dataSnapshot.getValue(ChatBean.class);
                            Log.e(TAG,"getMessageFromFirebaseUserMethod "+String.valueOf(chat));
                            viewer.onGetMessagesSuccess(chat);
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
                        }
                    });
                }
                else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(BookSellerUtil.JSON_CHAT)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            ChatBean chat = dataSnapshot.getValue(ChatBean.class);
                            Log.e(TAG,"getMessageFromFirebaseUserMethod "+String.valueOf(chat));
                            viewer.onGetMessagesSuccess(chat);
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}
                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
                        }
                    }); }
                else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                } }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Unable to get message: " + databaseError.getMessage());
            }
        });
    }


    public void sendMessage(String message) {
        addUserToChatListSender();
        addUserToChatListReciver();
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ChatBean chat = new ChatBean(sender,
                reciver.getEmail(),
                senderUid,
                reciver.getUid(),
                message,
                String.valueOf(System.currentTimeMillis()));

        storeMessageFirebase(chat);

        viewer.setClearSendTextField();
    }

    private void addUserToChatListSender() {
        mUser.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<Integer,String> temp ;
                        ArrayList<String> tempList;
                        if(dataSnapshot.child("chats").getValue()==null) {
                            tempList= new ArrayList<String>();
                        }
                        else {
                            tempList=(ArrayList)dataSnapshot.child("chats").getValue();
                        }
                        if(tempList!=null) {
                            if (!tempList.contains(reciver.getEmail()))
                                tempList.add(reciver.getEmail());
                        }
                        mUser.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).child("chats").setValue(tempList);
                        Log.d("LIST SENDER",String.valueOf(tempList));

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                }); }

    private void addUserToChatListReciver() {
        mUser.child(BookSellerUtil.JSON_USER).child(reciver.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> tempList;
                        if(dataSnapshot.child("chats").getValue()==null) {
                            tempList= new ArrayList<String>();
                        }
                        else {
                            tempList= (ArrayList)dataSnapshot.child("chats").getValue();
                        }
                        if(tempList!=null) {
                            if (!tempList.contains(sender.getEmail()))
                                tempList.add(sender.getEmail());
                        }

                        mChat.child(BookSellerUtil.JSON_USER).child(reciver.getUid()).child("chats").setValue(tempList);
                        Log.d("LIST RECIVER",String.valueOf(tempList));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }});
    }


    private void storeMessageFirebase(final ChatBean chat) {
        final String room_type_1 = chat.getSenderUid() + "_" + chat.getReceiverUid();
        final String room_type_2 = chat.getReceiverUid() + "_" + chat.getSenderUid();

        mChat.child(BookSellerUtil.JSON_CHAT).getRef().addListenerForSingleValueEvent((new ValueEventListener() {
            /*
            In this function data is added to firebase under the value name chats
            with in chats have object name room_type_1 or room_type_2 under the respective room
            there is another object store which has name timestamp at which message was sent and
            other this time stamp data is stored in form of cha bean
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                }
                else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_2).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                }
                else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    mChat.child(BookSellerUtil.JSON_CHAT).child(room_type_1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                    getMessageFromFirebaseUser();
                }
                // send push notification to the receiver
                sendPushNotificationToReceiver
                        (chat.sender,
                                chat.getMessage(),
                                chat.getSenderUid(),
                                sender.getToken(),
                                reciver.getToken());
                Toast.makeText(context,"Notification Sent",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        }));
    }


    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize().title(username).message(message).username(username).uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }
}
