package com.greyeg.tajr.adapters;

import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.models.BotBlock;

import java.util.ArrayList;

public class BotBlocksAdapter extends RecyclerView.Adapter<BotBlocksAdapter.BotBlocksHolder> {

    private ArrayList<BotBlock> botBlocks;

    public BotBlocksAdapter(ArrayList<BotBlock> botBlocks) {
        this.botBlocks = botBlocks;
    }

    @NonNull
    @Override
    public BotBlocksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_block_row,parent,false);
        return new BotBlocksHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BotBlocksHolder holder, int position) {
        BotBlock botBlock=botBlocks.get(position);
        holder.blockName.setText(botBlock.getName());
    }

    @Override
    public int getItemCount() {
        return botBlocks==null?0:botBlocks.size();
    }


    class BotBlocksHolder extends RecyclerView.ViewHolder {
        TextView blockName;
        BotBlocksHolder(@NonNull View itemView) {
            super(itemView);
            blockName=itemView.findViewById(R.id.bot_block_name);
        }
    }
}
