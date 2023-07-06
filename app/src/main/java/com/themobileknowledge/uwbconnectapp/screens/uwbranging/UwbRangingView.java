package com.themobileknowledge.uwbconnectapp.screens.uwbranging;

import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface UwbRangingView extends IBaseObservableView<UwbRangingView.Listener> {

    interface Listener {
        void onBackPressed();

        void onSelectAccessoryButtonClicked();

        void onSelectedAccessoryRemoveClicked();

        void onSelectedAccessorySelectAccessoryClicked();
    }

    void showSelectAccessoryText();

    void showSelectedAccessory(Accessory accessory);

    void updateSelectedAccessoryPosition(Accessory accessory, Position position);

    void clearAccessoryPosition();
}
