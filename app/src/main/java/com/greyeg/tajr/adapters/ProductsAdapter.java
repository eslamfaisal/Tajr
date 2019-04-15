package com.greyeg.tajr.adapters;

import android.app.Activity;
import android.app.Dialog;
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

    Activity context;
    List<ProDuct> proDucts;
    String orderId;
    String user_id;
    GetOrderInterface getOrderInterface;

    public ProductsAdapter(Activity context, List<ProDuct> proDucts, String orderId, String user_id, GetOrderInterface getOrderInterface) {
        this.context = context;
        this.proDucts = proDucts;
        this.orderId = orderId;
        this.user_id = user_id;
        this.getOrderInterface = getOrderInterface;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item_add, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {


        ProDuct proDuct = proDucts.get(i);
        holder.name.setText(proDuct.getProduct_name());
        holder.no.setText(proDuct.getItems_no());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    Toast.makeText(context, "لا يمكن مسح المنتج الرئيسي", Toast.LENGTH_SHORT).show();
                    return;
                }


                showDeleteDialog(orderId, String.valueOf(proDuct.getExtra_product_key()), proDuct.getProduct_id(), i);
            }
        });

//        if (i!=proDucts.size()-1){
//            holder.add_product.setVisibility(View.GONE);
//        }else {
//            holder.add_product.setOnClickListener(v -> {
//           getOrderInterface.getOrder();
//            });
//        }

    }

    private void showDeleteDialog(String orderId, String extra_product_key, String product_id, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_delete_poduct_dialog);
        dialog.show();
        TextView deleteBtn = dialog.findViewById(R.id.delete_product_btn);

        deleteBtn.setOnClickListener(v -> {
            deleteProduct(orderId, extra_product_key, product_id, position);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.cancel_btn).setOnClickListener(v -> dialog.dismiss());

    }

    @Override
    public int getItemCount() {
        return proDucts.size();
    }

    private void deleteProduct(String orderId, String extra_product_key, String product_id, int position) {
        Api api = BaseClient.getBaseClient().create(Api.class);

        api.deleteProduct(
                SharedHelper.getKey(context, LoginActivity.TOKEN),
                orderId,
                extra_product_key,
                user_id,
                product_id
        ).enqueue(new Callback<DeleteAddProductResponse>() {
            @Override
            public void onResponse(Call<DeleteAddProductResponse> call, Response<DeleteAddProductResponse> response) {

                if (response.body().getCode().equals("1200")) {
                    proDucts.remove(position);

                    notifyDataSetChanged();
                    if (proDucts.size()==1){
                        getOrderInterface.getOrder();
                    }

                }
                Toast.makeText(context, "" + response.body().getDetails(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DeleteAddProductResponse> call, Throwable t) {
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public interface GetOrderInterface {

        void getOrder();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView name, no;
        ImageView delete, add_product;

        public Holder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name);
            no = itemView.findViewById(R.id.items_no);
            delete = itemView.findViewById(R.id.delete);
            add_product = itemView.findViewById(R.id.add_product);

        }
    }


}
