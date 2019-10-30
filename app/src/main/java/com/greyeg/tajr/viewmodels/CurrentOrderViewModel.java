package com.greyeg.tajr.viewmodels;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.greyeg.tajr.models.AddReasonResponse;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.models.MainResponse;
import com.greyeg.tajr.repository.CancellationReasonsRepository;

public class CurrentOrderViewModel extends ViewModel {

    private MutableLiveData<CancellationReasonsResponse> cancellationReasonsResponse;
    private MutableLiveData<Boolean> cancellationReasonsLoading;

    private MutableLiveData<AddReasonResponse> addReason;
    private MutableLiveData<MainResponse> addReasonToOrder;


    //get order cancellation reason
    public void getCancellationReasons(String token){
        cancellationReasonsResponse= CancellationReasonsRepository.getInstance()
                .getCancellationReasons(token);
        cancellationReasonsLoading= CancellationReasonsRepository.getInstance().getIsCancellationReasonsLoading();

    }

    public MutableLiveData<CancellationReasonsResponse> getCancellationReasonsResponse() {
        return cancellationReasonsResponse;
    }

    public MutableLiveData<Boolean> getIsCancellationReasonsLoading() {
        return cancellationReasonsLoading;
    }

    public MutableLiveData<String> getCancellationReasonsLoadingError() {
        return CancellationReasonsRepository.getInstance().getCancellationReasonsLoadingError();
    }

    //submit new Order cancellation reason

    public MutableLiveData<AddReasonResponse> addReason(String token,String name){
        return addReason= CancellationReasonsRepository.getInstance()
                .addReason(token,name);
    }

    public MutableLiveData<AddReasonResponse> getAddReason() {
        return addReason;
    }

    public MutableLiveData<Boolean> getIsSubmittingReason() {
        return CancellationReasonsRepository.getInstance().getIsSubmittingReason();
    }

    public MutableLiveData<String> getReasonSubmittingError() {
        return CancellationReasonsRepository.getInstance().getReasonSubmittingError();
    }

    public void addReasonToOrder(String token, String orderId, String reason_id){
        addReasonToOrder=CancellationReasonsRepository.getInstance()
                .addReasonToOrder(token,orderId,reason_id);
    }

    public MutableLiveData<MainResponse> addReasonToOrder() {
        return addReasonToOrder;
    }

    public MutableLiveData<Boolean> getIsReasonAddingTOOrder() {
        return CancellationReasonsRepository.getInstance()
                .getIsReasonAddingTOOrder();
    }

    public MutableLiveData<String> getReasonAddingToOrderError() {
        return CancellationReasonsRepository.getInstance()
                .getReasonAddingToOrderError();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("REASONORDER", "onCleared: ");
    }
}
