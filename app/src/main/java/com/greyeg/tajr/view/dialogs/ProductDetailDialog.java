package com.greyeg.tajr.view.dialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.ExtraDataAdapter;
import com.greyeg.tajr.adapters.ExtraDataAdapter2;
import com.greyeg.tajr.adapters.ProductAdapter;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.AllProducts;
import com.greyeg.tajr.models.ExtraData;
import com.greyeg.tajr.models.OrderProduct;
import com.greyeg.tajr.models.ProductData;
import com.greyeg.tajr.models.ProductExtra;
import com.greyeg.tajr.repository.ProductsRepo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class ProductDetailDialog extends DialogFragment implements ProductAdapter.OnProductClicked {

    @BindView(R.id.productsRecycler)
    RecyclerView productsRecycler;
    @BindView(R.id.extra_data_recycler)
    RecyclerView extraDataRecycler;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.productImg)
    ImageView productImage;
    @BindView(R.id.quantity)
    TextView quantity;
    @BindView(R.id.increase)
    ImageView increment;
    @BindView(R.id.decrease)
    ImageView decrement;

    private OrderProduct product;
    private ExtraDataAdapter2 extraDataAdapter;
    private ProductAdapter productAdapter;

    public ProductDetailDialog(OrderProduct product) {
        this.product = product;
    }

    public ProductDetailDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View dialog=inflater.inflate(R.layout.product_detail_dialog,container,false);
        ButterKnife.bind(this,dialog);

        populateProductDetail();
        productAdapter=new ProductAdapter(getContext(),null,this);
        productsRecycler.setAdapter(productAdapter);
        productsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        productsRecycler.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));

        Log.d("extraDataAdapter", "onCreateView: ");

        return dialog;

    }

    @OnClick(R.id.updateProduct)
    public void updateProduct(){
        getExtraDataValues();
    }

    private Map<String, Object> getExtraDataValues(){
        Map<String,Object> values=new HashMap<>();

        ArrayList<ProductExtra> extraData=extraDataAdapter.getExtraData();

        for (int i = 0; i < extraDataAdapter.getItemCount(); i++) {



            View view = extraDataRecycler.getChildAt(i);
            if (Boolean.valueOf(extraData.get(i).Is_list())){
                Spinner spinner=view.findViewById(R.id.spinnerValue);

                if (Boolean.valueOf(extraData.get(i).getRequired())&&spinner.getSelectedItemPosition()==0){
                    Toast.makeText(getContext(), R.string.complete_fields_error, Toast.LENGTH_SHORT).show();
                    break;
                }

                if (spinner.getSelectedItemPosition()>0){
                    String value=extraData.get(i).getList().get(spinner.getSelectedItemPosition()-1);
                    values.put(extraData.get(i).getHtml(),value);
                }

            }else {

                EditText editText=view.findViewById(R.id.value);
                String value=editText.getText().toString();
                if (Boolean.valueOf(extraData.get(i).getRequired())&& TextUtils.isEmpty(value)){
                    Toast.makeText(getContext(), R.string.complete_fields_error, Toast.LENGTH_SHORT).show();
                    break;
                }
                values.put(extraData.get(i).getHtml(),value);
            }

        }
        return values;
    }

    private void populateProductDetail(){
        productName.setText(product.getName());
        Picasso.get()
                .load(product.getImage())
                .into(productImage);

        quantity.setText(String.valueOf(product.getItems_no()));

        increment.setOnClickListener(view -> {
            if (TextUtils.isEmpty(quantity.getText().toString()))return;
            int q= Integer.parseInt(quantity.getText().toString());
            q++;
            quantity.setText(String.valueOf(q));
        });
        decrement.setOnClickListener(view -> {
            if (TextUtils.isEmpty(quantity.getText().toString()))return;

            int q= Integer.parseInt(quantity.getText().toString());
            if (q==1)return;
            q--;
            quantity.setText(String.valueOf(q));


        });

        String token= SharedHelper.getKey(getContext(), LoginActivity.TOKEN);
        ProductsRepo
                .getInstance()
                .getProducts(token,null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<AllProducts>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Response<AllProducts> response) {
                        AllProducts products=response.body();
                        if (response.isSuccessful()&&products!=null){
                            productAdapter.addProducts(products.getProducts());

                        }else{
                            //todo handle case of no products
                            Toast.makeText(getContext(), R.string.error_getting_products, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), R.string.error_getting_products, Toast.LENGTH_SHORT).show();

                    }
                });

        extraDataAdapter=new ExtraDataAdapter2(getContext(),product.getExtras());
        extraDataRecycler.setAdapter(extraDataAdapter);
        extraDataRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }



    @Override
    public void onResume() {
        super.onResume();
        if (getDialog()!=null&&getDialog().getWindow()!=null)
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onProductClicked(ProductData product) {
        productName.setText(product.getProduct_name());

        Picasso
                .get()
                .load(product.getProduct_image())
                .into(productImage);

    }
}
