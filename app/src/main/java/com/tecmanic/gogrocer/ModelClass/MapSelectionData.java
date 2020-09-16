package com.tecmanic.gogrocer.ModelClass;

import com.squareup.moshi.Json;

public class MapSelectionData {

    @Json(name = "map_id")
    private String map_id;
    @Json(name = "mapbox")
    private String mapbox;
    @Json(name = "google_map")
    private String google_map;

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public String getMapbox() {
        return mapbox;
    }

    public void setMapbox(String mapbox) {
        this.mapbox = mapbox;
    }

    public String getGoogle_map() {
        return google_map;
    }

    public void setGoogle_map(String google_map) {
        this.google_map = google_map;
    }
}
