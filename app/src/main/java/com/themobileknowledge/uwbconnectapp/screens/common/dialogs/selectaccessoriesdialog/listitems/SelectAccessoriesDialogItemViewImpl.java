package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.listitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.SelectAccessoriesDialogItem;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseView;

public class SelectAccessoriesDialogItemViewImpl extends BaseView implements SelectAccessoriesDialogItemView {

    private final TextView mAccessoryName;
    private final TextView mAccessoryAddress;
    private final ImageView mAccessoryBondedImage;
    private final ImageView mAccessoryEditImage;
    private ConstraintLayout mAccessoryFrame;

    public SelectAccessoriesDialogItemViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.dialog_selectaccessories_listitem, parent, false));
        mAccessoryName = findViewById(R.id.selectaccessoriesdialog_listitem_name);
        mAccessoryAddress = findViewById(R.id.selectaccessoriesdialog_listitem_address);
        mAccessoryBondedImage = findViewById(R.id.selectaccessoriesdialog_listitem_imagebonded);
        mAccessoryEditImage = findViewById(R.id.selectaccessoriesdialog_listitem_imageedit);
        mAccessoryFrame = findViewById(R.id.selectaccessoriesdialog_listitem_background);
    }

    public void bindAccessory(SelectAccessoriesDialogItem selectAccessoriesDialogItem, final int position, final Listener listener) {
        if (selectAccessoriesDialogItem.getAlias() != null && !selectAccessoriesDialogItem.getAlias().isEmpty()) {
            mAccessoryName.setText(selectAccessoriesDialogItem.getAlias());
        } else {
            mAccessoryName.setText(selectAccessoriesDialogItem.getName());
        }

        mAccessoryAddress.setText(selectAccessoriesDialogItem.getMac());
        mAccessoryBondedImage.setVisibility(selectAccessoriesDialogItem.isBonded() ? View.VISIBLE : View.INVISIBLE);
        mAccessoryEditImage.setOnClickListener(view -> listener.onAccessoryEditClicked(position));
        mAccessoryFrame.setOnClickListener(view -> listener.onAccessoryClicked(position));
    }

    @Override
    public void setBackgroundSelected() {
        mAccessoryFrame.setBackgroundColor(getContext().getResources().getColor(R.color.selectaccessorydialogitem_backgroundselected));
    }

    @Override
    public void setBackgroundIdle() {
        mAccessoryFrame.setBackgroundColor(getContext().getResources().getColor(R.color.selectaccessorydialogitem_backgroundidle));
    }
}
