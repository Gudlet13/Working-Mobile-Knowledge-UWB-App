package com.themobileknowledge.uwbconnectapp.dependencyinjection;

import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

public class CompositionRoot {
    public String accessGlobalObject (){
        return "";
    }

    private DialogsEventBus mDialogsEventBus;

    public DialogsEventBus getDialogsEventBus() {
        if(mDialogsEventBus == null){
            mDialogsEventBus = new DialogsEventBus();
        }
        return mDialogsEventBus;
    }
}
