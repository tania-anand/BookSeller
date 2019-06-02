package com.example.hp.bookseller.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.RegistrationContract;
import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.presenter.RegistrationPresenter;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.UtilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class RegistrationActivity extends UtilActivity implements RegistrationContract.viewer,
        AdapterView.OnItemSelectedListener, View.OnClickListener, MyResponseConnectivity {


    private boolean updatemode=false;
    private RegistrationPresenter registrationPresenter;

    // for sign up activity create shared instance of FirebaseAuth
    private FirebaseAuth mAuth;
    // firebase Database
    private DatabaseReference mUsersDatabase;

    private static final String TAG = "EmailPassword";

    @BindView(R.id.submitbutton)
    Button btnsubmit;
    @BindView(R.id.spinner_college)
    Spinner college;
    @BindView(R.id.spinner_trait)
    Spinner trait;
    @BindView(R.id.textview1)
    TextView tittle;


    public void  initviews() {
        ButterKnife.bind(this);

        trait.setOnItemSelectedListener(this);
        college.setOnItemSelectedListener(this);
        btnsubmit.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initviews();

        Intent rcv=getIntent();
        updatemode=rcv.hasExtra("keyuser");

        registrationPresenter = new RegistrationPresenter(this,this,updatemode);
        registrationPresenter.initView(findViewById(android.R.id.content));

        if(updatemode) {
            registrationPresenter.setDataView((UserBean)rcv.getSerializableExtra("keyuser"));
            // for action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Details");
            tittle.setText("Edit Details");

        }


     }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId()==R.id.spinner_college) {
            registrationPresenter.setCollegeSpinnerData(position);
        }
        if(parent.getId()==R.id.spinner_trait) {
            registrationPresenter.setTraitSpinnerData(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch(id) {
            case R.id.submitbutton:
                if(isNetworkConnected(RegistrationActivity.this))
                    onSubmitForRegister();
                else
                    showMsg(RegistrationActivity.this);
                break;
        }
    }


    private void createAccount() {
        showDialog();
        final UserBean user =registrationPresenter.getUser();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser current_user = mAuth.getCurrentUser();

                            if(current_user!=null) {
                                user.setChats( new ArrayList<String>());
                                user.setUid(current_user.getUid());
                                Log.d("error", "createUserWithEmail:success"+user.toString());
                                mUsersDatabase.child(BookSellerUtil.JSON_USER).child(user.getUid()).setValue(user);


                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            dismissDialog();
                            Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            dismissDialog();

                        }

                    }
                });
        // [END create_user_with_email]
    }

    private void onSubmitForRegister(){
        showDialog();
        registrationPresenter.setUserData();
        registrationPresenter.onSubmitClick();
    }


    @Override
    public void registerClickSuccess() {
        if(!updatemode)
            createAccount();
        else {
            dismissDialog();
            Intent intent=new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void registerClickFailure() {
        dismissDialog();
    }

    @Override
    public void onMyResponseConnectivity(int i) {
        if(i==1){
            onSubmitForRegister();
        }else{
            showMsg(RegistrationActivity.this);
        }

    }
}
