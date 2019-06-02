package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.RegistrationContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistrationPresenter implements RegistrationContract.presenter {

    private Context context;
    private RegistrationContract.viewer viewer;
    private boolean updateMode;


    @BindView(R.id.edittextname)
    EditText edittxtname;

    @BindView(R.id.edittextphone)
    EditText edittxtphone;

    @BindView(R.id.edittextemail)
    EditText edittxtemail;

    @BindView(R.id.edittextpassword)
    EditText edittxtpassword;

    @BindView(R.id.edittextusername)
    EditText edittxtusername;

    @BindView(R.id.submitbutton)
    Button btnsubmit;

    @BindView(R.id.spinner_college)
    Spinner college;
    @BindView(R.id.spinner_trait)
    Spinner trait;


    private ArrayAdapter<String> collegeadapter,traitadapter;
    private UserBean user;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public RegistrationPresenter(Context context , RegistrationContract.viewer viewer, boolean updateMode){
        this.context = context;
        this.viewer = viewer;
        this.updateMode = updateMode;

        if(!updateMode)
            user = new UserBean();

    }

    public void initView(View view){
        ButterKnife.bind(this,view);
        setSpinnerData();
    }

    public void setUserData(){
        user.setName(edittxtname.getText().toString().trim());
        user.setPhone(edittxtphone.getText().toString().trim());
        user.setEmail(edittxtemail.getText().toString().trim());
        user.setUsername(edittxtusername.getText().toString().trim());
        user.setPassword(edittxtpassword.getText().toString().trim());
        user.setToken(FirebaseInstanceId.getInstance().getToken());
    }

    public void setDataView(UserBean updateuser){
        user = updateuser;
        edittxtname.setText(updateuser.getName());
        edittxtphone.setText(updateuser.getPhone());
        edittxtemail.setText(updateuser.getEmail());
        edittxtpassword.setText(updateuser.getPassword());
        edittxtusername.setText(updateuser.getUsername());

        for(int i=0;i<collegeadapter.getCount();i++) {
            if(collegeadapter.getItem(i).equals(updateuser.getCollegename())){
                college.setSelection(i); }
        }

        for(int x=0;x<traitadapter.getCount();x++){
            if(traitadapter.getItem(x).equals(updateuser.getTraitname())){
                trait.setSelection(x); }
        }

        btnsubmit.setText("Done");
        edittxtemail.setEnabled(false);


    }

    private void setSpinnerData(){
        collegeadapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1);

        collegeadapter.add("Select your college");
        collegeadapter.add("Guru Nanak Dev Engineering College");
        collegeadapter.add("Gulzar Group of Institutes");
        collegeadapter.add("Bhutta College Of Engineering&Technology");
        collegeadapter.add("RIMT-School of engineering");

        college.setAdapter(collegeadapter);

        traitadapter=new ArrayAdapter<>(context,android.R.layout.simple_list_item_1);

        traitadapter.add("Select your Trait");
        traitadapter.add("Mechanical");
        traitadapter.add("Computer Science ");
        traitadapter.add("IT");
        traitadapter.add("Electronics & Communication");
        traitadapter.add("Electrical");
        traitadapter.add("Civil");
        traitadapter.add("Production");

        trait.setAdapter(traitadapter);

    }

    public void onSubmitClick(){
        if(validateFields()){
            sendDataToServer();

        }else
            viewer.registerClickFailure();
    }

    private void clearfields() {
        edittxtname.setText(null);
        edittxtemail.setText(null);
        edittxtphone.setText(null);
        edittxtusername.setText(null);
        edittxtpassword.setText(null);
        college.setSelection(0);
        trait.setSelection(0);
    }

    private boolean validateFields() {
        boolean flag = true;

        if(user.getName().isEmpty()) {
            edittxtname.setError("Please Enter Name");
            flag = false;
        }
        if(user.getPhone().isEmpty()) {
            edittxtphone.setError("please enter phone");
            flag = false;
        }
        else {
            if(user.getPhone().length()!=10) {
                edittxtphone.setError("please enter a valid phone");
            }
        }

        if(user.getEmail().isEmpty()) {
            edittxtemail.setError(" Enter Email");
            flag = false;
        }
        else {
            if(!user.getEmail().contains("@") && !user.getEmail().contains(".")) {
                edittxtemail.setError(" Enter Valid Email");
                flag = false;
            }
        }

        if(user.getUsername().isEmpty()) {
            edittxtusername.setError("Enter a username");
            flag =false;
        }

        if(user.getPassword().isEmpty()) {
            edittxtpassword.setError("Enter Password");
            flag = false;
        }
        else {
            if((user.getPassword().length()<6)) {
                edittxtpassword.setError("Password must be 6 in length" + "");
                flag = false;
            }
        }

        if(user.getCollegename()==collegeadapter.getItem(0)) {
            Toast.makeText(context," select a college",Toast.LENGTH_LONG).show();
            flag = false;
        }

        if(user.getTraitname()==traitadapter.getItem(0)) {
            Toast.makeText(context," select a branch",Toast.LENGTH_LONG).show();
            flag = false;
        }

        return flag;
    }

    private void sendDataToServer(){
        String url;
        if(updateMode) {
            url=BookSellerUtil.URL_UPDATE;
        }
        else {
            url=BookSellerUtil.URL_INSERT;
        }

        JSONObject object= new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_NAME,user.getName());
            object.put(BookSellerUtil.KEY_PHONE,user.getPhone());
            object.put(BookSellerUtil.KEY_EMAIL,user.getEmail());
            object.put(BookSellerUtil.KEY_USERNAME,user.getUsername());
            object.put(BookSellerUtil.KEY_PASSWORD,user.getPassword());
            object.put(BookSellerUtil.KEY_COLLEGE,user.getCollegename());
            object.put(BookSellerUtil.KEY_TRAIT,user.getTraitname());


            // need to add the below element in database table modify scripts according to it
            if(!updateMode) {
                object.put(BookSellerUtil.KEY_UID,user.getUid());
                object.put(BookSellerUtil.KEY_TOKEN,user.getToken());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST,
                url,
                object,
                new MyResponseListener() {
                    @Override
                    public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                        // if success pass to display success
                        if(success)
                            viewer.registerClickSuccess();
                        else
                            viewer.registerClickFailure();

                    }

                    @Override
                    public void onMyResponseFailure(String message) {
                        // if failure show message
                        viewer.registerClickFailure();

                    }
                });
    }

    public void setCollegeSpinnerData(int position){
        if(collegeadapter.getCount()>=position)
            user.setCollegename(collegeadapter.getItem(position));

    }


    public void setTraitSpinnerData(int position){
        if(traitadapter.getCount()>=position)
            user.setTraitname(traitadapter.getItem(position));

    }


}
