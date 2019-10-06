package com.greyeg.tajr.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.models.CancellationReason;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancellationReasonsAdapter  extends RecyclerView.Adapter<CancellationReasonsAdapter.CancellationReasonsViewHolder> {

    private ArrayList<CancellationReason> cancellationReasons;

    public CancellationReasonsAdapter(ArrayList<CancellationReason> cancellationReasons) {
        this.cancellationReasons = cancellationReasons;
    }

    @NonNull
    @Override
    public CancellationReasonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cancel_reason,parent,false);
        return new CancellationReasonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancellationReasonsViewHolder holder, int position) {
        Log.d("CANCELLATIONN","name "+cancellationReasons.get(position).getReason_name());
        holder.reason.setText(cancellationReasons.get(position).getReason_name());
    }

    @Override
    public int getItemCount() {
        return cancellationReasons==null?0:cancellationReasons.size();
    }

    class CancellationReasonsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reason)
        TextView reason;
        CancellationReasonsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
