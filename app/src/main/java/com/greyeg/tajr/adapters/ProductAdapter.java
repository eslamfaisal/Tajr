package com.greyeg.tajr.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<ProductData> products;
    private OnProductClicked onProductClicked;
    private boolean isLoadingViewShown =false;
    public static final int PRODUCT_TYPE=1;
    public static final int LOADING_VIEW_TYPE=2;
    private int count=0;

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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Log.d("pagiiii", "onCreateViewHolder: "+viewType);
        if (viewType==PRODUCT_TYPE){
            View v= LayoutInflater.from(context).inflate(R.layout.product_row_item,parent,false);
            return new ProductsHolder(v);
        }else {

            View v= LayoutInflater.from(context).inflate(R.layout.loading_view,parent,false);
            return new LoadingViewHolder(v);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Log.d("pagiiii", "onBindViewHolder: "+position+" -->>> "+getItemCount());
        if (position>products.size()-1){
            Log.d("pagiiii", position+": exceed limit: "+getItemCount());
            return;
        }

        if (getItemViewType(position)==PRODUCT_TYPE){
            ProductsHolder productsHolder= (ProductsHolder) holder;
            ProductData product=products.get(position);
            productsHolder.product_name.setText(product.getProduct_name());
            Picasso
                    .get()
                    .load(product.getProduct_image())
                    .into(productsHolder.product_image);
        }else {
            Log.d("pagiiii","AD WAS SHOWN");
                    LoadingViewHolder loadingViewHolder= (LoadingViewHolder) holder;
            loadingViewHolder.loadMorePB.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return products==null?0:products.size()+count;
    }

    public void addProducts(List<ProductData> products, String page) {
        isLoadingViewShown=false;
        count=0;
        this.products.addAll(products);
        notifyDataSetChanged();

    }

    public void setLoadingView(){
        Log.d("pagiiii", "setLoadingView: ");
        isLoadingViewShown=true;
        count=1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //Log.d("pagiiii", "getItemViewType: "+position+" -> "+(position==products.size()-1&&isLoadingViewShown));
        //return position==products.size()-1?LOADING_VIEW_TYPE:PRODUCT_TYPE;
        if (position==products.size()-1&&isLoadingViewShown)
            return LOADING_VIEW_TYPE;
        else return PRODUCT_TYPE;

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

    class LoadingViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.loadMorePB)
        ProgressBar loadMorePB;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

    public interface OnProductClicked{
        void onProductClicked(ProductData productData);
    }
}
