package com.themobileknowledge.uwbconnectapp.screens.distancealert.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem.DistanceAlertItemView;
import com.themobileknowledge.uwbconnectapp.screens.distancealert.listitem.DistanceAlertItemViewImpl;

import java.util.ArrayList;
import java.util.List;

public class RangeRecyclerAdapter extends RecyclerView.Adapter<RangeRecyclerAdapter.MyViewHolder> {

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final DistanceAlertItemViewImpl mView;

        public MyViewHolder(DistanceAlertItemViewImpl itemView) {
            super(itemView.getRootView());
            mView = itemView;
        }
    }

    private final ViewFactory mViewFactory;
    private List<DistanceAlertRecyclerItem> mNotificationList = new ArrayList<>();
    private DistanceAlertItemView.Listener mListener;

    public RangeRecyclerAdapter(ViewFactory viewFactory) {
        mViewFactory = viewFactory;
    }

    public void bindNotificationList(List<DistanceAlertRecyclerItem> notificationList) {
        mNotificationList = notificationList;
        notifyDataSetChanged();
    }

    public void bindNotificationListListener(DistanceAlertItemView.Listener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DistanceAlertItemViewImpl view = mViewFactory.getDistanceAlertItemView(parent);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mView.bindNotification(mNotificationList.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
}
