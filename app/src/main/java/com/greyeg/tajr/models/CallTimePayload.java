package com.greyeg.tajr.models;

public class CallTimePayload {

    private String token;
    private String order_id;
    private String history_line;
    private String duration;
    private String verb;
    private String count;

    public CallTimePayload(String token, String order_id, String history_line, String duration, String verb, String count) {
        this.token = token;
        this.order_id = order_id;
        this.history_line = history_line;
        this.duration = duration;
        this.verb = verb;
        this.count = count;
    }

    public CallTimePayload(String token, String order_id, String history_line, String duration) {
        this.token = token;
        this.order_id = order_id;
        this.history_line = history_line;
        this.duration = duration;
    }

    public String getToken() {
        return token;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getHistory_line() {
        return history_line;
    }

    public String getDuration() {
        return duration;
    }

    public String getVerb() {
        return verb;
    }

    public String getCount() {
        return count;
    }
}
