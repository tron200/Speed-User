package com.speed.user.models;

public class ChangeDestRequest {

    String d_address;
    String d_latitude;
    String d_longitude;
    String status;
    String request_id;

    public String getD_address() {
        return d_address;
    }

    public void setD_address(String d_address) {
        this.d_address = d_address;
    }

    public String getD_latitude() {
        return d_latitude;
    }

    public void setD_latitude(String d_latitude) {
        this.d_latitude = d_latitude;
    }

    public String getD_longitude() {
        return d_longitude;
    }

    public void setD_longitude(String d_longitude) {
        this.d_longitude = d_longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
