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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.adapters.ExtraDataAdapter;
import com.greyeg.tajr.adapters.ExtraDataAdapter2;
import com.greyeg.tajr.models.ExtraData;
import com.greyeg.tajr.models.OrderProduct;
import com.greyeg.tajr.models.ProductExtra;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductDetailDialog extends DialogFragment {

    @BindView(R.id.productsRecycler)
    RecyclerView productsRecycler;
    @BindView(R.id.extra_data_recycler)
    RecyclerView extraDataRecycler;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.productImg)
    ImageView productImage;

    private OrderProduct product;
    private ExtraDataAdapter2 extraDataAdapter;

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
}
