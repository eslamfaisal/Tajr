package com.greyeg.tajr.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.models.MonthPoints;

import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {

    List<MonthPoints> monthPoints;
    Activity context;

    public PointsAdapter(List<MonthPoints> monthPoints, Activity context) {
        this.monthPoints = monthPoints;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_mointh_point_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        MonthPoints points = monthPoints.get(i);

        holder.rank.setText(points.getRank());
        holder.cash.setText(points.getCash());
        holder.points.setText(points.getPoints());

    }

    @Override
    public int getItemCount() {
        return monthPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView points;
        TextView rank;
        TextView cash;
        TextView month;

        View mainView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            points = mainView.findViewById(R.id.points);
            cash = mainView.findViewById(R.id.cash);
            month = mainView.findViewById(R.id.month);
            rank = mainView.findViewById(R.id.rank);
        }
    }
}
