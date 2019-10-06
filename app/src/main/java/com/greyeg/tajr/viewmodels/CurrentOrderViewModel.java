package com.greyeg.tajr.viewmodels;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.greyeg.tajr.models.AddReasonResponse;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.repository.OrdersRepository;

public class CurrentOrderViewModel extends ViewModel {

    private MutableLiveData<CancellationReasonsResponse> cancellationReasonsResponse;
    private MutableLiveData<Boolean> cancellationReasonsLoading;

    private MutableLiveData<AddReasonResponse> addReason;


    //get order cancellation reason
    public void getCancellationReasons(String token){
        cancellationReasonsResponse= OrdersRepository.getInstance()
                .getCancellationReasons(token);
        cancellationReasonsLoading=OrdersRepository.getInstance().getIsCancellationReasonsLoading();

    }

    public MutableLiveData<CancellationReasonsResponse> getCancellationReasonsResponse() {
        return cancellationReasonsResponse;
    }

    public MutableLiveData<Boolean> getIsCancellationReasonsLoading() {
        return cancellationReasonsLoading;
    }

    public MutableLiveData<String> getCancellationReasonsLoadingError() {
        return OrdersRepository.getInstance().getCancellationReasonsLoadingError();
    }

    //submit new Order cancellation reason

    public MutableLiveData<AddReasonResponse> addReason(String token,String name){
        return addReason= OrdersRepository.getInstance()
                .addReason(token,name);
    }

    public MutableLiveData<AddReasonResponse> getAddReason() {
        return addReason;
    }

    public MutableLiveData<Boolean> getIsSubmittingReason() {
        return OrdersRepository.getInstance().getIsSubmittingReason();
    }

    public MutableLiveData<String> getReasonSubmittingError() {
        return OrdersRepository.getInstance().getReasonSubmittingError();
    }
}
