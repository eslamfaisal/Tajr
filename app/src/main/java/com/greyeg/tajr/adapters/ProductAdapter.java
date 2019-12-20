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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductsHolder> {


    private Context context;
    private ArrayList<ProductData> products;
    private OnProductClicked onProductClicked;

    public ProductAdapter(Context context, ArrayList<ProductData> products) {
        this.context = context;
        if (products==null)products=new ArrayList<>();
        this.products = products;
    }

    public ProductAdapter(Context context, ArrayList<ProductData> products, OnProductClicked onProductClicked) {
        this.context = context;
        if (products==null)products=new ArrayList<>();
        this.products = products;
        this.onProductClicked = onProductClicked;
    }

    @NonNull
    @Override
    public ProductsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.product_row_item,parent,false);
        return new ProductsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsHolder holder, int position) {
        ProductData product=products.get(position);

        holder.product_name.setText(product.getProduct_name());
        Picasso
                .get()
                .load(product.getProduct_image())
                .into(holder.product_image);
    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size();
    }

    public void addProducts(List<ProductData> products) {
        this.products.addAll(products);
        notifyDataSetChanged();

    }

    class ProductsHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.product_image)
        ImageView product_image;
        @BindView(R.id.product_name)
        TextView product_name;


        public ProductsHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onProductClicked.onProductClicked(products.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnProductClicked{
        void onProductClicked(ProductData productData);
    }
}
