package com.themobileknowledge.uwbconnectapp.screens.tracker;


import android.opengl.GLSurfaceView;

import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Coordinates;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Direction;

public interface TrackerView extends IBaseObservableView<TrackerView.Listener> {

    interface Listener {
        void onBackPressed();

        void onSelectAccessoriesButtonClicked();
    }

    GLSurfaceView getSurfaceView();

    void hideStartDemoButtonVisibility();

    void positionARCoreAccessory(Accessory accessory, Position position, Coordinates coordinates, Direction direction);

    void unknownPositionARCoreAccessory(Accessory accessory, Position position);

    void removeARCoreAccessory(Accessory accessory);

    void removeARCoreAccessories();
}
