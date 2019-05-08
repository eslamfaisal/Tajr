package com.greyeg.tajr.order.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greyeg.tajr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentOrderFragment extends Fragment {

    private static final String TAG = "CurrentOrderFragment";
    View mainView;

    public CurrentOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_current_order, container, false);

        initLabels();

        return mainView;
    }

    private void initLabels() {
        LinearLayout linearLayout = mainView.findViewById(R.id.order_fields);

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            LinearLayout chiledLinearLayout = linearLayout.findViewById(linearLayout.getChildAt(i).getId());
            if (chiledLinearLayout != null)
                chiledLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        runAnimation(chiledLinearLayout.getChildAt(0).getId(), chiledLinearLayout.getChildAt(1).getId());
                    }
                });
        }
    }

    private void runAnimation(int id1, int id2) {

        TextView tv = (TextView) mainView.findViewById(id1);
        FrameLayout bg = (FrameLayout) mainView.findViewById(id2);

        if (tv.getVisibility() == View.VISIBLE) {
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_sheet_fad_out);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.GONE);
            bg.setBackgroundResource(R.drawable.ic_background_gray);
        } else {
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.bottom_sheet_fad_in);
            a.reset();
            tv.clearAnimation();
            tv.startAnimation(a);
            tv.setVisibility(View.VISIBLE);
            bg.setBackgroundResource(R.drawable.ic_background_gray_down);

        }

    }
}
