package com.themobileknowledge.uwbconnectapp.screens.distancealert;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.DistanceAlertRecyclerItem;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.RangeRecyclerAdapter;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem.DistanceAlertItemView;

import java.util.List;

public class DistanceAlertViewImpl extends BaseObservableView<DistanceAlertView.Listener> implements DistanceAlertView {

    private RecyclerView mDistanceAlertRecycler;
    private RangeRecyclerAdapter mDistanceAlertAdapter;

    private List<DistanceAlertRecyclerItem> mRecyclerItemList;

    public DistanceAlertViewImpl(LayoutInflater inflater, ViewGroup parent, ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_distancealert, parent, false));

        mDistanceAlertRecycler = findViewById(R.id.distancealert_recycler);
        mDistanceAlertRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mDistanceAlertAdapter = new RangeRecyclerAdapter(viewFactory);
        mDistanceAlertRecycler.setAdapter(mDistanceAlertAdapter);
    }

    private void notifyOnBackPressed() {
        for (Listener listener : getListeners()) {
            listener.onBackPressed();
        }
    }

    private void notifyMenuSettingsClicked() {
        for (Listener listener : getListeners()) {
            listener.onMenuSettingsClicked();
        }
    }

    private void notifyAccessoryEditClicked(Accessory accessory) {
        for (Listener listener : getListeners()) {
            listener.onAccessoryEditClicked(accessory);
        }
    }

    @Override
    public void bindDistanceAlertItemList(List<DistanceAlertRecyclerItem> recyclerItemList) {
        mRecyclerItemList = recyclerItemList;
        mDistanceAlertAdapter.bindNotificationList(mRecyclerItemList);
        mDistanceAlertAdapter.bindNotificationListListener(new DistanceAlertItemView.Listener() {
            @Override
            public void onAccessoryEditClicked(Accessory accessory) {
                notifyAccessoryEditClicked(accessory);
            }
        });
    }

    @Override
    public void update() {
        mDistanceAlertAdapter.notifyDataSetChanged();
    }

    @Override
    public void bindMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_actionbar_distancealert, menu);
    }

    @Override
    public void onMenuItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                notifyOnBackPressed();
                break;
            case R.id.menu_distancealert_settings:
                notifyMenuSettingsClicked();
                break;
            default:
                break;
        }
    }
}
