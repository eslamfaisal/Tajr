package com.greyeg.tajr.models;

import java.io.Serializable;

public class OutgoingCallData  implements Serializable {
    private int state;
    private String number;

    public OutgoingCallData() {
    }

    public OutgoingCallData(int state, String number) {
        this.state = state;
        this.number = number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
