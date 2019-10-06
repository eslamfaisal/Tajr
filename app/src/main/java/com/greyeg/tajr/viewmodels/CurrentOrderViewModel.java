package com.greyeg.tajr.viewmodels;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.repository.OrdersRepository;

public class CurrentOrderViewModel extends ViewModel {

    private MutableLiveData<CancellationReasonsResponse> cancellationReasonsResponse;
    private MutableLiveData<Boolean> cancellationReasonsLoading;


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

}
