package com.greyeg.tajr.models;

import androidx.annotation.NonNull;

public class ExtraData {

    private String name;
    private String type;
    private String request_name;
    private String details;


    public ExtraData(String name, String type, String request_name, String details) {
        this.name = name;
        this.type = type;
        this.request_name = request_name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRequest_name() {
        return request_name;
    }

    public String getDetails() {
        return details;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "name "+name+
                "type "+type+
                "request_name "+request_name+
                "details "+details
                ;
    }
}
