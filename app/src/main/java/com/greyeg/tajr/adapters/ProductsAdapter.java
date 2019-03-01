package com.greyeg.tajr.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.DeleteAddProductResponse;
import com.greyeg.tajr.models.ProDuct;
import com.greyeg.tajr.server.Api;
import com.greyeg.tajr.server.BaseClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.Holder> {

    Context context;
    List<ProDuct> proDucts;
    String orderId;

    public ProductsAdapter(Context context, List<ProDuct> proDucts, String orderId) {
        this.context = context;
        this.proDucts = proDucts;
        this.orderId = orderId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        ProDuct proDuct =  proDucts.get(i);
        holder.name.setText( proDuct.getProduct_name());
        holder.no.setText( proDuct.getItems_no());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i==0){
                    Toast.makeText(context, "لا يمكن مسح المنتج الرئيسي", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteProduct(orderId,String.valueOf(proDuct.getExtra_product_key()),proDuct.getProduct_id(),i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return proDucts.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        TextView name,no;
        ImageView delete;
        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            no = itemView.findViewById(R.id.items_no);
            delete =itemView.findViewById(R.id.delete);

        }
    }

    private void deleteProduct(String orderId,String extra_product_key,String product_id,int position){
     Api api=   BaseClient.getBaseClient().create(Api.class);

     api.deleteProduct(
             SharedHelper.getKey(context, LoginActivity.TOKEN),
             orderId,
             extra_product_key,
             product_id
             ).enqueue(new Callback<DeleteAddProductResponse>() {
         @Override
         public void onResponse(Call<DeleteAddProductResponse> call, Response<DeleteAddProductResponse> response) {

             if (response.body().getCode().equals("1200")){
                 proDucts.remove(position);
                 notifyItemRemoved(position);

             }
             Toast.makeText(context, ""+response.body().getDetails(), Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onFailure(Call<DeleteAddProductResponse> call, Throwable t) {
             Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
         }
     });

    }


}
