package com.themobileknowledge.uwbconnectapp.screens.distancealert;

import android.view.Menu;
import android.view.MenuItem;

import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.DistanceAlertRecyclerItem;

import java.util.List;

public interface DistanceAlertView extends IBaseObservableView<DistanceAlertView.Listener> {

    interface Listener {
        void onBackPressed();

        void onMenuSettingsClicked();

        void onAccessoryEditClicked(Accessory accessory);
    }

    void bindMenu(Menu menu);

    void onMenuItemSelected(MenuItem item);

    void bindDistanceAlertItemList(List<DistanceAlertRecyclerItem> distanceAlertItemList);

    void update();
}
