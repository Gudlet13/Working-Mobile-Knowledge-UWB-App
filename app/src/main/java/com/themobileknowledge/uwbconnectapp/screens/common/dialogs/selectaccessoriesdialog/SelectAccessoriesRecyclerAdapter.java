package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.listitems.SelectAccessoriesDialogItemView;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog.listitems.SelectAccessoriesDialogItemViewImpl;
import com.themobileknowledge.uwbconnectapp.screens.common.toastshelper.ToastsHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectAccessoriesRecyclerAdapter extends RecyclerView.Adapter<SelectAccessoriesRecyclerAdapter.MyViewHolder> {

    interface Listener {
        void onAccessoryEditClicked(int position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final SelectAccessoriesDialogItemViewImpl mView;

        public MyViewHolder(SelectAccessoriesDialogItemViewImpl itemView) {
            super(itemView.getRootView());
            mView = itemView;
        }
    }

    private final ViewFactory mViewFactory;
    private final ToastsHelper mToastHelper;
    private Listener mListener;
    private List<SelectAccessoriesDialogItem> mAccessoriesList = new ArrayList<>();
    private int mMaxAllowedAccessories = 1;
    private int mEditPosition = -1;

    public SelectAccessoriesRecyclerAdapter(ToastsHelper toastsHelper, ViewFactory viewFactory) {
        mViewFactory = viewFactory;
        mToastHelper = toastsHelper;
    }

    public void setMaxAllowedAccessories(int maxAllowedAccessories) {
        mMaxAllowedAccessories = maxAllowedAccessories;
        notifyDataSetChanged();
    }

    public void bindAccessoriesList(List<SelectAccessoriesDialogItem> accessoriesList) {
        mAccessoriesList = accessoriesList;
        notifyDataSetChanged();
    }

    public void bindListener(final Listener listener) {
        mListener = listener;
        notifyDataSetChanged();
    }

    public void expiredAccessoriesPositions(final List<Integer> expiredAccessoriesPositions) {

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SelectAccessoriesDialogItemViewImpl view = mViewFactory.getSelectAccessoriesDialogItemView(parent);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mView.bindAccessory(mAccessoriesList.get(position), position, new SelectAccessoriesDialogItemView.Listener() {
            @Override
            public void onAccessoryClicked(int position) {
                if (mAccessoriesList.get(position).isSelected()) {
                    mAccessoriesList.get(position).setSelected(false);
                } else {
                    if (mMaxAllowedAccessories == 1) {
                        for (SelectAccessoriesDialogItem item : mAccessoriesList) {
                            item.setSelected(false);
                        }

                        mAccessoriesList.get(position).setSelected(true);
                    } else {
                        int selectedPositions = 0;
                        for (SelectAccessoriesDialogItem item : mAccessoriesList) {
                            if (item.isSelected()) {
                                selectedPositions++;
                            }
                        }

                        if (selectedPositions < mMaxAllowedAccessories) {
                            mAccessoriesList.get(position).setSelected(true);
                        } else {
                            mToastHelper.notifyGenericMessage("Maximum number of accessories selected");
                        }
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onAccessoryEditClicked(int position) {
                mEditPosition = position;
                mListener.onAccessoryEditClicked(position);
            }
        });

        if (mAccessoriesList.get(position).isSelected()) {
            holder.mView.setBackgroundSelected();
        } else {
            holder.mView.setBackgroundIdle();
        }
    }

    @Override
    public int getItemCount() {
        return mAccessoriesList.size();
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (SelectAccessoriesDialogItem item : mAccessoriesList) {
            if (item.isSelected()) {
                selectedPositions.add(mAccessoriesList.indexOf(item));
            }
        }

        return selectedPositions;
    }

    public int getEditPosition() {
        return mEditPosition;
    }
}
