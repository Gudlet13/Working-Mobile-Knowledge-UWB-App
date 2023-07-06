package com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem;

import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.DistanceAlertRecyclerItem;

public interface DistanceAlertItemView {

    interface Listener {
        void onAccessoryEditClicked(Accessory accessory);
    }

    void bindNotification(DistanceAlertRecyclerItem item, Listener listener);
}
