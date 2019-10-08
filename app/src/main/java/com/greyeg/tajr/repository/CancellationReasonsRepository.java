package com.greyeg.tajr.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.greyeg.tajr.models.AddReasonResponse;
import com.greyeg.tajr.models.CancellationReasonsResponse;
import com.greyeg.tajr.server.BaseClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersRepository {

    private static OrdersRepository ordersRepository;

    //get order cancellation reason
    private MutableLiveData<CancellationReasonsResponse> cancellationReasonsResponse;
    private MutableLiveData<Boolean> isCancellationReasonsLoading=new MutableLiveData<>();
    private MutableLiveData<String> cancellationReasonsLoadingError=new MutableLiveData<>();

    //submit new Order cancellation reason
    private MutableLiveData<AddReasonResponse> addReasonResponse;
    private MutableLiveData<Boolean>  isSubmittingReason=new MutableLiveData<>();
    private MutableLiveData<String> reasonSubmittingError=new MutableLiveData<>();



    public static OrdersRepository getInstance() {
        return ordersRepository==null?new OrdersRepository():ordersRepository;
    }

    private OrdersRepository(){
    }

    public MutableLiveData<CancellationReasonsResponse> getCancellationReasons(String token){
        cancellationReasonsResponse=new MutableLiveData<>();
        isCancellationReasonsLoading.setValue(true);
        Log.d("CANCELLATIONN","from repo "+isCancellationReasonsLoading.getValue());
        BaseClient
                .getService()
                .getCancellationReasons(token)
                .enqueue(new Callback<CancellationReasonsResponse>() {
                    @Override
                    public void onResponse(Call<CancellationReasonsResponse> call, Response<CancellationReasonsResponse> response) {
                        isCancellationReasonsLoading.setValue(false);
                        Log.d("CANCELLATIONN","from repo "+isCancellationReasonsLoading.getValue());
                        if (response.isSuccessful()&&response.body()!=null)
                            cancellationReasonsResponse.setValue(response.body());
                        else
                            cancellationReasonsLoadingError.setValue("Error while getting cancellation error \n code"+response.code());
                    }

                    @Override
                    public void onFailure(Call<CancellationReasonsResponse> call, Throwable t) {
                        isCancellationReasonsLoading.setValue(false);
                        cancellationReasonsLoadingError.setValue(t.getMessage());
                    }
                });

        return cancellationReasonsResponse;

    }

    public MutableLiveData<Boolean> getIsCancellationReasonsLoading() {
        Log.d("CANCELLATIONN","from repo getter "+(isCancellationReasonsLoading.getValue()));
        return isCancellationReasonsLoading;
    }

    public MutableLiveData<String> getCancellationReasonsLoadingError() {
        return cancellationReasonsLoadingError;
    }



    //submit new Order cancellation reason

    public MutableLiveData<AddReasonResponse> addReason(String token,String name){
        addReasonResponse=new MutableLiveData<>();
        isSubmittingReason.setValue(true);
        BaseClient
                .getService()
                .submitNewCancellationReason(token,name)
                .enqueue(new Callback<AddReasonResponse>() {
                    @Override
                    public void onResponse(Call<AddReasonResponse> call, Response<AddReasonResponse> response) {
                        if (response.isSuccessful()&&response.body()!=null)
                            addReasonResponse.setValue(response.body());
                        else
                            reasonSubmittingError.setValue("Error while submitting new Reason \n code"+response.code());


                        isSubmittingReason.setValue(false);

                    }

                    @Override
                    public void onFailure(Call<AddReasonResponse> call, Throwable t) {
                        isSubmittingReason.setValue(false);
                        reasonSubmittingError.setValue(t.getMessage());
                    }
                });

        return addReasonResponse;
    }

    public MutableLiveData<Boolean> getIsSubmittingReason() {
        return isSubmittingReason;
    }

    public MutableLiveData<String> getReasonSubmittingError() {
        return reasonSubmittingError;
    }
}
