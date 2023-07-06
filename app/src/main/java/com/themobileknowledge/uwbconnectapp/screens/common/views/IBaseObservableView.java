package com.themobileknowledge.uwbconnectapp.screens.common.views;

public interface IBaseObservableView<ListenerType> extends IBaseView{

    void registerListener (ListenerType listener);
    void unregisterListener (ListenerType listener);

}
