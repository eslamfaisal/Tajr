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
import com.greyeg.tajr.models.OrderProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.CartItemHolder>{

    private Context context;
    private ArrayList<OrderProduct> products;
    private OnProductItemEvent onProductItemEvent;

    public OrderProductsAdapter(Context context, ArrayList<OrderProduct> products) {
        this.context = context;
        this.products = products;
    }

    public OrderProductsAdapter(Context context, ArrayList<OrderProduct> products, OnProductItemEvent onProductItemEvent) {
        this.context = context;
        this.products = products;
        this.onProductItemEvent = onProductItemEvent;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new CartItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        OrderProduct product=products.get(position);

        holder.name.setText(product.getName());
        Picasso.get()
                .load(product.getImage())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }

    class CartItemHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.item_img)
        ImageView thumbnail;
        @BindView(R.id.increase)
        ImageView increaseQuantity;
        @BindView(R.id.decrease)
        ImageView decreaseQuantity;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.quantity)
        TextView quantity;
        @BindView(R.id.name)
        TextView name;

        CartItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(view -> {
                onProductItemEvent.OnProductItemClicked(products.get(getAdapterPosition()));
            });

            delete.setOnClickListener(view -> {

            });


            increaseQuantity.setOnClickListener(view -> {

            });

            decreaseQuantity.setOnClickListener(view -> {

            });
        }
    }

    public interface OnProductItemEvent{
        void onCartItemDeleted(int productId);
        void onCartItemQuantityIncrease(int productId,int quantity);
        void onCartItemQuantityDecrease(int productId,int quantity);
        void OnProductItemClicked(OrderProduct product);
    }
}


