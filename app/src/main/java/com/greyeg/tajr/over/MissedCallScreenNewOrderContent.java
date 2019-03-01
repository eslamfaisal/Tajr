package com.greyeg.tajr.over;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.EmptyCallActivity;

import butterknife.BindView;
import io.mattcarroll.hover.Content;

public class MissedCallScreenNewOrderContent implements Content {

    private final Context mContext;
    private View mContent;


    @BindView(R.id.product)
    Spinner product;

    public MissedCallScreenNewOrderContent(@NonNull Context mContext) {
        this.mContext = mContext.getApplicationContext();
    }

    @NonNull
    @Override
    public View getView() {

        if (null == mContent) {
            mContent = LayoutInflater.from(mContext).inflate(R.layout.layout_missed_call_no_order_found, null);
            onViewCreated(mContent);
            // We present our desire to be non-fullscreen by using WRAP_CONTENT for height.  This
            // preference will be honored by the Hover Menu to make our content only as tall as we
            // want to be.
            mContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        return mContent;
    }


    public void onViewCreated(@NonNull View view) {

        view.findViewById(R.id.go_order_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EmptyCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                mContext.stopService(new Intent(mContext,MissedCallNoOrderService.class));
            }
        });

    }

    @Override
    public boolean isFullscreen() {
        return false;
    }

    @Override
    public void onShown() {
        // No-op.
    }

    @Override
    public void onHidden() {
        // No-op.
    }
}
