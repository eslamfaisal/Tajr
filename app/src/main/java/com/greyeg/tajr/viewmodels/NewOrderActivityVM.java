package com.greyeg.tajr.viewmodels;

import androidx.lifecycle.MutableLiveData;

import com.greyeg.tajr.models.UserWorkTimeResponse;
import com.greyeg.tajr.repository.WorkTimeRepo;

public class NewOrderActivityVM {

    private MutableLiveData<UserWorkTimeResponse> sendWorkTime;
    private MutableLiveData<Boolean> isWorkTimeSending;
    private MutableLiveData<String> workTimeSendingError;

    public MutableLiveData<UserWorkTimeResponse> SendWorkTime(String token,
                                                                 String activity,
                                                                 String user_id,
                                                                 String action) {

        sendWorkTime=WorkTimeRepo.getInstance()
                .sendWorkTime( token, activity, user_id, action);


        return sendWorkTime;
    }

    public MutableLiveData<Boolean> getIsWorkTimeSending() {
        return WorkTimeRepo.getInstance().getIsWorkTimeSending();
    }

    public MutableLiveData<String> getWorkTimeSendingError() {
        return WorkTimeRepo.getInstance().getWorkTimeSendingError();
    }
}
