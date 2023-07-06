package com.themobileknowledge.uwbconnectapp.screens.common.dialogs;

import com.themobileknowledge.uwbconnectapp.screens.common.BaseObservable;

public class DialogsEventBus extends BaseObservable<DialogsEventBus.Listener> {

    public interface Listener {
        void onDialogEvent(Object event);
    }

    public void postEvent(Object event) {
        for (Listener listener : getListeners()) {
            listener.onDialogEvent(event);
        }
    }
}
