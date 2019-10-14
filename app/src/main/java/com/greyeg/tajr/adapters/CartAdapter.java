package com.greyeg.tajr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.models.ProductData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemHolder> {

    private Context context;
    private ArrayList<ProductData> products;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new CartItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        // todo show error image
        ProductData product=products.get(position);
        Picasso.get()
                .load(product.getProduct_image())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }

    public void addCartItem(ProductData productData){
        this.products.add(productData);
        notifyDataSetChanged();
    }

    class CartItemHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img)
        ImageView thumbnail;
        @BindView(R.id.increase)
        ImageView increaseQuantity;
        @BindView(R.id.decrease)
        ImageView decreaseQuantity;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.quantity)
        TextView quantity;

        CartItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
