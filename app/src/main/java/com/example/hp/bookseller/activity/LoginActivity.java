package com.example.hp.bookseller.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.LoginContract;
import com.example.hp.bookseller.interfaces.MyResponseConnectivity;
import com.example.hp.bookseller.presenter.LoginPresenter;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.UtilActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends UtilActivity implements  LoginContract.view, MyResponseConnectivity {
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;


    @BindView(R.id.edittext_username)
    EditText edittxtusername;
    @BindView(R.id.edittext_password)
    EditText edittxtpassword;
    @BindView(R.id.buttonlogin)
    Button btnlogin;
    @BindView(R.id.textview_registeractivity)
    TextView txtregisteractivity;

    String username,password;
    LoginPresenter loginPresenter;

    private void init() {
        ButterKnife.bind(this);
        loginPresenter = new LoginPresenter(this,this);
        initMyResponseConnectivityListener(LoginActivity.this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected(LoginActivity.this))
                   onLoginClick();
                else
                    showMsg(LoginActivity.this);
            }
        });
    }

    private void onLoginClick(){
        showDialog();
        username = edittxtusername.getText().toString();
        password = edittxtpassword.getText().toString();
        loginPresenter.loginOnClick(username, password);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        mAuth = FirebaseAuth.getInstance();
    }

    private void signInFireBase() {
        showDialog();
        mAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            handler.sendEmptyMessage(100);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void registerClickHandler(View view) {
        Intent intent=new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    void clearFields() {
        edittxtusername.setText("");
        edittxtpassword.setText("");
    }

     @SuppressLint("HandlerLeak")
     Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==101) {
                finish();
            }
            if(msg.what==100) {
                updateFirebaseToken();
            }
        }
    };

    DatabaseReference mFirebaseSetting;
    private void updateFirebaseToken() {
        mFirebaseSetting = FirebaseDatabase.getInstance().getReference();
        mFirebaseSetting.child(BookSellerUtil.JSON_USER).child(mAuth.getCurrentUser().getUid()).child("token").setValue(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public void loginClickSuccess() {
        dismissDialog();
        signInFireBase();

        Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
        handler.sendEmptyMessageDelayed(101,6000);

        Intent intentmain= new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intentmain);
    }

    @Override
    public void loginClickFailure(String message) {
        dismissDialog();
        clearFields();
        if(!message.equals(""))
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMyResponseConnectivity(int i) {
        if(i==1){
            onLoginClick();
        }else{
            showMsg(LoginActivity.this);
        }

    }
}
