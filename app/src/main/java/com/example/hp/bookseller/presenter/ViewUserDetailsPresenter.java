package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.activity.ViewUserDetails;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewUserDetailsPresenter {

    @BindView(R.id.textview_email)
    TextView textViewemail;

    @BindView(R.id.textview_phone)
    TextView textViewphone;

    @BindView(R.id.textview_username)
    TextView textViewusername;

    @BindView(R.id.textview_college)
    TextView textViewcollege;

    @BindView(R.id.textview_trait)
    TextView textViewtrait;

    private  Context context;

    public ViewUserDetailsPresenter(Context context ,View view){
        this.context = context;
        ButterKnife.bind(this,view);
    }

    public void setData(){
        UserBean user= SharedPreferencesUtil.getInstance(context).getUserDetails();
        Log.d("USER DETAILS",user.toString());

        textViewemail.setText(user.getEmail());
        textViewphone.setText(user.getPhone());
        textViewusername.setText(user.getName());
        textViewcollege.setText(user.getCollegename());
        textViewtrait.setText(user.getTraitname());

    }





}
