package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.ViewBookDetailsContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.model.UserBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;
import com.example.hp.bookseller.utils.ParseResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBookDetailsPresenter  {

    private Context context;
    private ViewBookDetailsContract.viewer viewer;
    private View view;
    private BookBean bookBean;
    private UserBean user;

    @BindView(R.id.VBImage)
    ImageView imgbook;
    @BindView(R.id.BName)
    TextView txtviewname;
    @BindView(R.id.BCondition)
    TextView txtviewcondition;
    @BindView(R.id.BPrice)
    TextView txtviewprice;
    @BindView(R.id.BAuthor)
    TextView txtviewauthor;
    @BindView(R.id.BPublisher)
    TextView txtviewpublisher;
    @BindView(R.id.BType)
    TextView txtviewtype;

    public ViewBookDetailsPresenter(Context context , ViewBookDetailsContract.viewer viewer , View view){
        this.context = context;
        this.viewer = viewer;
        this.view = view;

        ButterKnife.bind(this,view);

    }


    public void setData(BookBean bookBean){

        this.bookBean = bookBean;
        Picasso.with(context).load(bookBean.getImage()).into(imgbook);
        txtviewname.setText(bookBean.getName());
        txtviewauthor.setText("By "+bookBean.getAuthor());
        txtviewpublisher.setText(bookBean.getPublisher());
        txtviewprice.setText("\u20B9"+bookBean.getPrice());
        txtviewcondition.setText(bookBean.getCondition());
        txtviewtype.setText(bookBean.getTrait());
    }

    public void retreiveUserDetails(){
        JSONObject object = new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_USERNAME,bookBean.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST,BookSellerUtil.URL_RETRIEVE_PHONENO, object, new MyResponseListener() {
            @Override
            public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                user = new ParseResponse().getUser(jsonObject);
                viewer.onRetreiveUserDetailSuccess();
            }

            @Override
            public void onMyResponseFailure(String message) {
                viewer.onRetreiveUserDetailFailure();
            }
        });
    }

    public UserBean getUser(){
        return user;
    }

}
