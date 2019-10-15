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
import com.greyeg.tajr.models.CartItem;
import com.greyeg.tajr.models.ProductData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartItemHolder> {

    private Context context;
    private ArrayList<CartItem> cartItems;
    private OnCartItemEvent onCartItemEvent;

    public CartAdapter(Context context) {
        this.context = context;
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, OnCartItemEvent onCartItemEvent) {
        this.context = context;
        this.cartItems = cartItems;
        this.onCartItemEvent = onCartItemEvent;
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

        ProductData product=cartItems.get(position).getProduct();
        Picasso.get()
                .load(product.getProduct_image())
                .into(holder.thumbnail);

        holder.quantity.setText(String.valueOf(cartItems.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartItems==null?0:cartItems.size();
    }

    public void addCartItem(CartItem cartItem){
        if (cartItems==null) cartItems=new ArrayList<>();
        this.cartItems.add(cartItem);
        notifyDataSetChanged();
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

        CartItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartItems.remove(cartItems.get(getAdapterPosition()));
                    notifyDataSetChanged();
                }
            });


            increaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartItem cartItem=cartItems.get(getAdapterPosition());
                    cartItem.setQuantity(cartItem.getQuantity()+1);
                    notifyDataSetChanged();
                }
            });

            decreaseQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CartItem cartItem=cartItems.get(getAdapterPosition());
                    if (cartItem.getQuantity()==1) return;
                    cartItem.setQuantity(cartItem.getQuantity()-1);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface OnCartItemEvent{
        void onCartItemDeleted(int productId);
        void onCartItemQuantityIncrease(int productId);
        void onCartItemQuantityDecrease(int productId);
    }
}
