package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.listitems;

import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialogItem;

public interface SelectAccessoriesDialogItemView {

    interface Listener {
        void onAccessoryClicked(int position);
        void onAccessoryEditClicked(int position);
    }

    void bindAccessory(SelectAccessoriesDialogItem item, int position, Listener listener);

    void setBackgroundSelected ();

    void setBackgroundIdle ();
}
