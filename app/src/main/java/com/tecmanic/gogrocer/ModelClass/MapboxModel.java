package com.tecmanic.gogrocer.ModelClass;

public class MapboxModel {

    private String status;
    private String message;
    private MapboxDataModel data;

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

    public MapboxDataModel getData() {
        return data;
    }

    public void setData(MapboxDataModel data) {
        this.data = data;
    }
}
