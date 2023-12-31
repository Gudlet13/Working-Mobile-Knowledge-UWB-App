package com.themobileknowledge.uwbconnectapp.screens.common.views;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseObservableView <ListenerType> extends BaseView implements IBaseObservableView<ListenerType>{

    private Set<ListenerType> mListeners = new HashSet<>();

    @Override
    public void registerListener(ListenerType listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        mListeners.remove(listener);
    }

    protected Set<ListenerType> getListeners() {
        return Collections.unmodifiableSet(mListeners);
    }

}
