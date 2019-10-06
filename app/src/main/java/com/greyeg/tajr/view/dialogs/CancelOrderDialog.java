package com.greyeg.tajr.view.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.cancel_order_dialog,container,false);
        ButterKnife.bind(this,v);
        currentOrderViewModel= ViewModelProviders.of(this).get(CurrentOrderViewModel.class);
        String token= SharedHelper.getKey(getContext(), LoginActivity.TOKEN);

        //getLifecycle().addObserver(currentOrderViewModel);

        currentOrderViewModel.getCancellationReasons(token);
        observeCancellationReasonsResponse();
        observeCancellationReasonsLoading();
        observeCancellationReasonsLoadingError();

        return v;
    }

    private void observeCancellationReasonsResponse(){
        currentOrderViewModel
                .getCancellationReasonsResponse()
                .observe(this, new Observer<CancellationReasonsResponse>() {
                    @Override
                    public void onChanged(CancellationReasonsResponse cancellationReasonsResponse) {
                        populateReasons(cancellationReasonsResponse.getReasons());
                    }
                });
    }
    private void observeCancellationReasonsLoading(){
        currentOrderViewModel
                .getIsCancellationReasonsLoading()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        Log.d("CANCELLATIONN","observer work "+aBoolean);
                        if (aBoolean!=null&&aBoolean)
                            progressBar.setVisibility(View.VISIBLE);
                            else
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void observeCancellationReasonsLoadingError(){
        currentOrderViewModel
                .getCancellationReasonsLoadingError()
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
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
        if (getDialog().getWindow()!=null)
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
