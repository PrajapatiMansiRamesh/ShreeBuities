package com.tecmanic.gogrocer.ModelClass;

import java.io.Serializable;
import java.util.ArrayList;

public class NewCategoryDataModel implements Serializable {

    private String product_id;
    private String cat_id;
    private String product_name;
    private String product_image;
    private ArrayList<NewCategoryVarientList> varients;

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public ArrayList<NewCategoryVarientList> getVarients() {
        return varients;
    }

    public void setVarients(ArrayList<NewCategoryVarientList> varients) {
        this.varients = varients;
    }
}
