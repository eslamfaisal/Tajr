package com.greyeg.tajr.models;

import java.util.ArrayList;

public class UserTimePayload {

    private String token;
    private ArrayList<Activity> activity;

    public String getToken() {
        return token;
    }

    public ArrayList<Activity> getActivity() {
        return activity;
    }
}
