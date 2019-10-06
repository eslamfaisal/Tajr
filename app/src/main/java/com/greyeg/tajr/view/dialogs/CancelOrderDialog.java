package com.greyeg.tajr.view.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greyeg.tajr.R;
import com.greyeg.tajr.activities.LoginActivity;
import com.greyeg.tajr.adapters.CancellationReasonsAdapter;
import com.greyeg.tajr.helper.SharedHelper;
import com.greyeg.tajr.models.CancellationReason;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.viewmodels.CurrentOrderViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CancelOrderDialog extends DialogFragment {

    private CurrentOrderViewModel currentOrderViewModel;
    @BindView(R.id.cancel_order_recycler)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.cancel_order_dialog,container,false);
        ButterKnife.bind(this,v);
        currentOrderViewModel= ViewModelProviders.of(getActivity()).get(CurrentOrderViewModel.class);
        String token= SharedHelper.getKey(getContext(), LoginActivity.TOKEN);
        currentOrderViewModel.getCancellationReasons(token);
        observeCancellationReasonsResponse();

        return v;
    }

    private void observeCancellationReasonsResponse(){
        currentOrderViewModel
                .getCancellationReasonsResponse()
                .observe(getActivity(), new Observer<CancellationReasonsResponse>() {
                    @Override
                    public void onChanged(CancellationReasonsResponse cancellationReasonsResponse) {
                        populateReasons(cancellationReasonsResponse.getReasons());
                        Log.d("CANCELLATIONN", "onChanged: "+cancellationReasonsResponse.getData());
                    }
                });
    }

    private void populateReasons(ArrayList<CancellationReason> reasons){
        CancellationReasonsAdapter adapter=new CancellationReasonsAdapter(reasons);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
