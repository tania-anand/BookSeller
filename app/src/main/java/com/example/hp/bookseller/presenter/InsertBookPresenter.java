package com.example.hp.bookseller.presenter;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.hp.bookseller.R;
import com.example.hp.bookseller.contract.InsertBookContract;
import com.example.hp.bookseller.interfaces.MyResponseListener;
import com.example.hp.bookseller.model.BookBean;
import com.example.hp.bookseller.utils.BookSellerUtil;
import com.example.hp.bookseller.utils.NetworkCall;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InsertBookPresenter implements InsertBookContract.presenter {

    private Context context;
    private InsertBookContract.viewer viewer;

    @BindView(R.id.BName)
    EditText BName;
    @BindView(R.id.BAuthor)
    EditText BAuthor;
    @BindView(R.id.BPublisher)
    EditText BPublisher;
    @BindView(R.id.BPrice)
    EditText BPrice;
    @BindView(R.id.spinner_trait1)
    Spinner trait;

    private ArrayAdapter<String> traitadapter;
    private BookBean bean;

    public InsertBookPresenter(Context context,InsertBookContract.viewer viewer ,View view){
        this.context = context;
        this.viewer = viewer;

        ButterKnife.bind(this,view);
        initView();
    }

    private void initView(){
        traitadapter=new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1);
        traitadapter.add("Select Type ");
        traitadapter.add("Mechanical");
        traitadapter.add("Computer Science ");
        traitadapter.add("IT");
        traitadapter.add("Electronics & Communication");
        traitadapter.add("Electrical");
        traitadapter.add("Civil");
        traitadapter.add("Production");
        traitadapter.add("Other");
        trait.setAdapter(traitadapter);

    }

    public ArrayAdapter <String> getTraitadapter() {
        return traitadapter;
    }

    private   boolean validations() {
        boolean flag = true;
        if(BName.getText().toString().isEmpty()) {
            BName.setError("Book Name Required");
            flag = false;
        }
        if(BPublisher.getText().toString().isEmpty()) {
            BPublisher.setError("Publisher Name Required");
            flag = false;
        }
        if(BAuthor.getText().toString().isEmpty()) {
            BAuthor.setError("Author Required");
            flag = false;
        }
        if(BPrice.getText().toString().isEmpty()) {
            BPrice.setError(" Price Required");
            flag = false;
        }
        if(bean.getTrait().equals("Select Type ")){
            Toast.makeText(context,"select a book type",Toast.LENGTH_LONG).show();
            flag = false;
        }
        if(bean.getCondition()==null){
            Toast.makeText(context,"choose condition of your book",Toast.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }

    private void insertBookServer(){
        JSONObject object = new JSONObject();
        try {
            object.put(BookSellerUtil.KEY_ITEM_IMAGE,bean.getImage());
            object.put(BookSellerUtil.KEY_ITEM_NAME,bean.getName());
            object.put(BookSellerUtil.KEY_ITEM_AUTHOR,bean.getAuthor());
            object.put(BookSellerUtil.KEY_ITEM_PUBLISHER,bean.getPublisher());
            object.put(BookSellerUtil.KEY_ITEM_CONDITION,bean.getCondition());
            object.put(BookSellerUtil.KEY_ITEM_PRICE,bean.getPrice());
            object.put(BookSellerUtil.KEY_USERNAME,bean.getUserName());
            object.put(BookSellerUtil.KEY_ITEM_TRAIT,bean.getTrait());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetworkCall networkCall = NetworkCall.getMyDB();
        networkCall.initRequestQueue(context);
        networkCall.processRequest(Request.Method.POST,BookSellerUtil.URL_ITEMINSERT,object, new MyResponseListener() {
            @Override
            public void onMyResponseSuccess(boolean success, JSONObject jsonObject) {
                if(success)
                    viewer.onInsertSuccess();
                else
                    viewer.onInsertFailure();
            }
            @Override
            public void onMyResponseFailure(String message) {
                viewer.onInsertFailure();
            }
        });
    }

    public void insertBook(BookBean bean){
        this.bean = bean;
        if(validations()){
          insertBookServer();
        }
    }

}
