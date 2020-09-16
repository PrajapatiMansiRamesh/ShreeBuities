package com.tecmanic.gogrocer.ModelClass;

public class MapboxDataModel {
    private String map_id;
    private String mapbox_api;

    public String getId() {
        return map_id;
    }

    public void setId(String map_id) {
        this.map_id = map_id;
    }

    public String getMap_api_key() {
        return mapbox_api;
    }

    public void setMap_api_key(String map_api_key) {
        this.mapbox_api = map_api_key;
    }
}
