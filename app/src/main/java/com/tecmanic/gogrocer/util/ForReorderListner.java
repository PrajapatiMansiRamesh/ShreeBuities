package com.tecmanic.gogrocer.util;

import com.tecmanic.gogrocer.ModelClass.NewPastOrderSubModel;

import java.util.ArrayList;
import java.util.List;

public interface ForReorderListner {

    void onReorderClick(ArrayList<NewPastOrderSubModel> pastOrderSubModelArrayList);
}
