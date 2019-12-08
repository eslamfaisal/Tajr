package com.greyeg.tajr.adapters;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.models.ExtraData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExtraDataAdapter extends RecyclerView.Adapter<ExtraDataAdapter.ExtraDetailViewHolder> {

    private ArrayList<ExtraData> extraData;


    public ExtraDataAdapter(ArrayList<ExtraData> extraData) {
        this.extraData = extraData;
    }

    @NonNull
    @Override
    public ExtraDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.extra_data_layout,parent,false);
        return new ExtraDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtraDetailViewHolder holder, int position) {
        ExtraData extra=extraData.get(position);
        handleInputs(holder,extra);
    }

    @Override
    public int getItemCount() {
        return extraData==null?0:extraData.size();
    }

    private void handleInputs(ExtraDetailViewHolder holder, ExtraData extra){
        TextView label=holder.label;
        EditText value=holder.value;

        label.setText(extra.getName());
        value.setHint(extra.getDetails());


        if (extra.getType().equals("number"))
        value.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    class ExtraDetailViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.value)
        EditText value;
        @BindView(R.id.field_label)
        TextView label;
        ExtraDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
