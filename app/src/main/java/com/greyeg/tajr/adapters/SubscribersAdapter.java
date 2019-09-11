package com.greyeg.tajr.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.greyeg.tajr.R;
import com.greyeg.tajr.models.Subscriber;
import com.greyeg.tajr.server.BaseClient;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.SubscriberHolder> {

    Context context;
    private ArrayList<Subscriber> subscribers;
    private OnSubscriberSelected onSubscriberSelected;



    public SubscribersAdapter(Context context, ArrayList<Subscriber> subscribers, OnSubscriberSelected onSubscriberSelected) {
        this.context = context;
        this.subscribers = subscribers;
        this.onSubscriberSelected = onSubscriberSelected;
    }

    @NonNull
    @Override
    public SubscriberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context)
                .inflate(R.layout.subscriber_row,parent,false);
        return new SubscriberHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriberHolder holder, int position) {
        Subscriber subscriber=subscribers.get(position);
        holder.name.setText(subscriber.getName());
        getImageUrl(subscriber.getImg(),holder.image);


    }

    @Override
    public int getItemCount() {
        return subscribers==null?0:subscribers.size();
    }

    class SubscriberHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView image;
        public SubscriberHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.subscriberName);
            image=itemView.findViewById(R.id.subscriberImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSubscriberSelected.onSubscriberSelected(
                            subscribers.get(getAdapterPosition()).getPsid()
                    ,subscribers.get(getAdapterPosition())
                                    .getId());
                }
            });
        }
    }

    private void getImageUrl(String url,ImageView img){
        BaseClient
                .getService()
                .getImageUrl(url).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String content_type=response.headers().get("content-type");
                        int i= content_type.indexOf('/');
                        String extension=content_type.substring(i+1);
                        if (i==-1)
                            img.setImageResource(R.drawable.ic_error_black_24dp);

                        String fullUrl=url+"."+extension;

                        Picasso.get()
                                .load(fullUrl)
                                .error(R.drawable.ic_error_black_24dp)
                                .into(img);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("IMAGEEEEEE", "onFailure: "+t.getMessage());

                    }
                });
    }

    public interface OnSubscriberSelected{
        void onSubscriberSelected(String psid,String userId);
    }
}
