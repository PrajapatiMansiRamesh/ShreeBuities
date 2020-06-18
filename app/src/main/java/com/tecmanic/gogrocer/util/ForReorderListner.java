package com.tecmanic.gogrocer.util;

import com.tecmanic.gogrocer.ModelClass.NewPendingDataModel;

import java.util.ArrayList;

public interface ForReorderListner {

    void onReorderClick(ArrayList<NewPendingDataModel> pastOrderSubModelArrayList);
}
