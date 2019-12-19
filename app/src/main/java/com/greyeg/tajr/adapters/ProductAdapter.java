package com.greyeg.tajr.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;

import java.util.ArrayList;

import butterknife.BindView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsHolder> {


    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ProductsHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.product_image)
        ImageView product_image;
        @BindView(R.id.product_name)
        ImageView product_name;


        public ProductsHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
