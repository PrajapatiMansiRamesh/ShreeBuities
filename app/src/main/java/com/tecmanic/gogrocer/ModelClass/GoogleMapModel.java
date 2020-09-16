package com.tecmanic.gogrocer.ModelClass;

public class GoogleMapModel {
    private String status;
    private String message;
    private GoogleMapDataModel data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GoogleMapDataModel getData() {
        return data;
    }

    public void setData(GoogleMapDataModel data) {
        this.data = data;
    }
}
