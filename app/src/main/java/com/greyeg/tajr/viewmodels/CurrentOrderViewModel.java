package com.greyeg.tajr.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.repository.OrdersRepository;

public class CurrentOrderViewModel extends ViewModel {

    private MutableLiveData<CancellationReasonsResponse> cancellationReasonsResponse;


    public void getCancellationReasons(String token){
        cancellationReasonsResponse= OrdersRepository.getInstance()
                .getCancellationReasons(token);
    }

    public MutableLiveData<CancellationReasonsResponse> getCancellationReasonsResponse() {
        return cancellationReasonsResponse;
    }

    public MutableLiveData<Boolean> getIsCancellationReasonsLoading() {
        return OrdersRepository.getInstance().getIsCancellationReasonsLoading();
    }

    public MutableLiveData<String> getCancellationReasonsLoadingError() {
        return OrdersRepository.getInstance().getCancellationReasonsLoadingError();
    }

}
