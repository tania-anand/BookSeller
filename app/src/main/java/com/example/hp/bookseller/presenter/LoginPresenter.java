package com.example.hp.bookseller.presenter;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;

import com.android.volley.Request;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.LoginContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.example.hp.bookseller.utils.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginPresenter implements LoginContract.presenter {
    private static final String TAG = "LoginPresenter";

    @BindView(R.id.edittext_username)
    EditText edUsername;

    @BindView(R.id.edittext_password)
    EditText edPassword;

    private LoginContract.view viewer;
    private Context context;

    public LoginPresenter(Context context ,LoginContract.view viewer){
        this.viewer = viewer;
        this.context = context;
        ButterKnife.bind(this,(Activity)context);
    }

    public void loginOnClick(final String username, final String password){
        if(validateFields()){
            /*
            check from server if given user exists or not.
            */
            JSONObject object= new JSONObject();
            try {
                object.put("USERNAME",username);
                object.put("PASSWORD",password);
                object.put(BookSellerUtil.KEY_TOKEN, FirebaseInstanceId.getInstance().getToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            NetworkCall networkCall = NetworkCall.getMyDB();
            networkCall.initRequestQueue(context);
            networkCall.processRequest(Request.Method.POST,
                    BookSellerUtil.URL_LOGINCHECK,
                    object,
                    new MyResponseListener() {
                    @Override
                    public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                        // if success pass to display success
                        if(success) {
                            SharedPreferencesUtil.getInstance(context).setLoginDetails(username,password);
                            viewer.loginClickSuccess();
                        }
                        else
                            viewer.loginClickFailure("Error in login");
                    }

                    @Override
                    public void onMyResponseFailure(String message) {
                        // if failure show message
                        viewer.loginClickFailure(message);
                    }
            });
        }else{
            viewer.loginClickFailure("");
        }

    }

    private boolean validateFields() {
        boolean flag = true;
        if(edUsername.toString().isEmpty()) {
            edUsername.setError("Enter Email Id");
            flag = false;
        }
        else {
            if(!edUsername.getText().toString().contains("@") && !edUsername.getText().toString().contains(".")) {
                edUsername.setError("Enter Valid Email");
                flag = false;
            }
        }
        if(edUsername.toString().isEmpty()) {
            edPassword.setError("Enter Password");
            flag = false;
        }
        return flag;
    }

}
