package com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseView;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters.DistanceAlertRecyclerItem;

public class DistanceAlertItemViewImpl extends BaseView implements DistanceAlertItemView {

    private final TextView mAccessoryName;
    private final TextView mAccessoryAddress;
    private final TextView mAccessoryDistance;
    private final TextView mThresholdDistance;
    private final ImageView mAccessoryEdit;
    private final MaterialCardView mNotificationFrame;
    private final View mThresholdFrame;

    public DistanceAlertItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.distancealert_listitem, parent, false));
        mAccessoryName = findViewById(R.id.distancealertitem_name);
        mAccessoryAddress = findViewById(R.id.distancealertitem_address);
        mAccessoryDistance = findViewById(R.id.distancealertitem_distance);
        mNotificationFrame = findViewById(R.id.distancealertitem_notificationframe);
        mAccessoryEdit = findViewById(R.id.distancealertitem_imageedit);
        mThresholdFrame = findViewById(R.id.distancealertitem_thresholdframe);
        mThresholdDistance = findViewById(R.id.distancealert_threshold_distance);
    }

    public void bindNotification(DistanceAlertRecyclerItem item, Listener listener) {
        if (item.isThresholdLine()) {
            mNotificationFrame.setVisibility(View.GONE);
            mThresholdFrame.setVisibility(View.VISIBLE);
            mThresholdDistance.setText(item.getThresholdLimit() + " cms");
        } else {
            mNotificationFrame.setVisibility(View.VISIBLE);
            mThresholdFrame.setVisibility(View.GONE);
            if (item.getNotification().getAccessory().getAlias() != null && !item.getNotification().getAccessory().getAlias().isEmpty()) {
                mAccessoryName.setText(item.getNotification().getAccessory().getAlias());
            } else {
                mAccessoryName.setText(item.getNotification().getAccessory().getName());
            }
            mAccessoryAddress.setText(item.getNotification().getAccessory().getMac());
            mAccessoryDistance.setText(item.getNotification().getDistance() + " cms");
            mNotificationFrame.setStrokeColor(getBackgroundColorFromThreshold(item.getNotification().getLowerThreshold()));
            mAccessoryEdit.setOnClickListener(view -> listener.onAccessoryEditClicked(item.getNotification().getAccessory()));
        }
    }

    private int getBackgroundColorFromThreshold(int lowerThreshold) {
        switch (lowerThreshold) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.YELLOW;
            case 2:
            default:
                return Color.RED;
        }
    }
}
