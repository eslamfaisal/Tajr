package com.greyeg.tajr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greyeg.tajr.R;
import com.greyeg.tajr.adapters.PointsAdapter;
import com.greyeg.tajr.models.MonthPoints;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PointsFragment extends Fragment {

    @BindView(R.id.points_recycler_view)
    RecyclerView pointsRecyclerView;

    LinearLayoutManager poitsLayoutManager;
    List<MonthPoints> monthPoints;
    PointsAdapter adapter;
    public PointsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_points, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        monthPoints = new ArrayList<>();
        monthPoints.add(new MonthPoints("يناير","50 جنية","150 نقطة","لا يوجد"));
        monthPoints.add(new MonthPoints("يناير","50 جنية","150 نقطة","لا يوجد"));
        monthPoints.add(new MonthPoints("يناير","50 جنية","150 نقطة","لا يوجد"));
        adapter = new PointsAdapter(monthPoints,getActivity());
        poitsLayoutManager = new LinearLayoutManager(getActivity());
        pointsRecyclerView.setLayoutManager(poitsLayoutManager);
        pointsRecyclerView.setAdapter(adapter);
    }

}
