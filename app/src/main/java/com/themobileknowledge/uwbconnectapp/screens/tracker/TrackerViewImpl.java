package com.themobileknowledge.uwbconnectapp.screens.tracker;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Coordinates;
import com.themobileknowledge.uwbconnectapp.screens.tracker.arcore.model.Direction;

public class TrackerViewImpl extends BaseObservableView<TrackerView.Listener> implements TrackerView, View.OnClickListener {

    private GLSurfaceView mSurfaceView;
    private RelativeLayout mRelativeLayout;
    private FloatingActionButton mFloatingActionButton;

    public TrackerViewImpl(LayoutInflater inflater, ViewGroup parent, ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_tracker, parent, false));
        mFloatingActionButton = findViewById(R.id.tracker_selectaccessory_button);
        mRelativeLayout = findViewById(R.id.tracker_positionview);
        mSurfaceView = findViewById(R.id.tracker_surfaceview);
        mFloatingActionButton.setOnClickListener(this);
    }

    private void notifySelectAccessoryClicked() {
        for (TrackerView.Listener listener : getListeners()) {
            listener.onSelectAccessoriesButtonClicked();
        }
    }

    @Override
    public GLSurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public void positionARCoreAccessory(Accessory accessory, Position position, Coordinates coordinates, Direction direction) {
        // Remove accessory so that we can print it back on the screen
        removeARCoreAccessory(accessory);

        // Inflate view from xml and update textviews with accessory and position information
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout arcoreitem = (RelativeLayout) layoutInflater.inflate(R.layout.tracker_arcoreitem, mRelativeLayout, false);
        ImageView arcoreitem_image = arcoreitem.findViewById(R.id.tracker_arcoreitem_image);
        TextView arcoreitem_name = arcoreitem.findViewById(R.id.tracker_arcoreitem_name);
        TextView arcoreitem_position = arcoreitem.findViewById(R.id.tracker_arcoreitem_position);

        if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
            arcoreitem_name.setText(accessory.getAlias());
        } else {
            arcoreitem_name.setText(accessory.getName());
        }

        arcoreitem_position.setText(getString(R.string.tracker_arcoreitem_position_distance, (int) (position.getDistance() * 100)));
        // arcoreitem_position.setText(getString(R.string.tracker_arcoreitem_position_distanceandangle,
        //        (int) (position.getDistance() * 100), (int) position.getAzimuth(), (int) position.getElevation()));

        // Update imageview
        switch (direction) {
            case IS_VISIBLE:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_visible);
                break;

            case IS_RIGHT:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_arrow_right);
                break;

            case IS_LEFT:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_arrow_left);
                break;

            case IS_UP:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_arrow_up);
                break;

            case IS_DOWN:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_arrow_down);
                break;

            case IS_UNKNOWN:
            default:
                arcoreitem_image.setImageResource(R.drawable.ic_tracker_unknown);
                break;
        }

        // Calculate the 2D position on the screen based on the coordinates and apply the required margins
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Update margins
        switch (direction) {
            case IS_VISIBLE:
                layoutParams.setMargins(coordinates.getX(), coordinates.getY(), 0, 0);
                break;

            case IS_RIGHT:
                layoutParams.setMargins(0, coordinates.getY(), 20, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);

                break;

            case IS_LEFT:
                layoutParams.setMargins(20, coordinates.getY(), 0, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                break;

            case IS_UP:
                layoutParams.setMargins(coordinates.getX(), 20, 0, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                break;

            case IS_DOWN:
                layoutParams.setMargins(coordinates.getX(), 0, 0, 20);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

                break;

            case IS_UNKNOWN:
            default:
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 20, 0, 0);
                break;
        }

        // Add new view to parent layout
        arcoreitem.setLayoutParams(layoutParams);
        arcoreitem.setId(getViewIdFromAccessory(accessory));
        mRelativeLayout.addView(arcoreitem);
    }

    @Override
    public void unknownPositionARCoreAccessory(Accessory accessory, Position position) {
        // Remove accessory so that we can print it back on the screen
        removeARCoreAccessory(accessory);

        // Inflate view from xml and update textviews with accessory and position information
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout arcoreitem = (RelativeLayout) layoutInflater.inflate(R.layout.tracker_arcoreitem, mRelativeLayout, false);
        ImageView arcoreitem_image = arcoreitem.findViewById(R.id.tracker_arcoreitem_image);
        TextView arcoreitem_name = arcoreitem.findViewById(R.id.tracker_arcoreitem_name);
        TextView arcoreitem_position = arcoreitem.findViewById(R.id.tracker_arcoreitem_position);

        if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
            arcoreitem_name.setText(accessory.getAlias());
        } else {
            arcoreitem_name.setText(accessory.getName());
        }

        arcoreitem_position.setText(getString(R.string.tracker_arcoreitem_position_distance, (int) (position.getDistance() * 100)));
        // arcoreitem_position.setText(getString(R.string.tracker_arcoreitem_position_distanceandangle, (int) (position.getDistance() * 100),
        //        (int) position.getAzimuth(), (int) position.getElevation()));
        arcoreitem_image.setImageResource(R.drawable.ic_tracker_unknown);

        // There might be more than one element in unknown position, so we needto make sure that views are not overlapped
        int viewPositionIndex = 0;
        if (mRelativeLayout.getChildCount() > 1) {
            int id = getViewIdFromAccessory(accessory);
            for (int i = 0; i < mRelativeLayout.getChildCount(); i++) {
                if (id >= Math.abs(mRelativeLayout.getChildAt(i).getId())) {
                    viewPositionIndex++;
                }
            }
        }

        // Calculate the 2D position on the screen based on the coordinates and apply the required margins
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 20 + (viewPositionIndex * 200), 0, 0);

        // Add new view to parent layout
        arcoreitem.setLayoutParams(layoutParams);
        arcoreitem.setId(getViewIdFromAccessory(accessory));
        mRelativeLayout.addView(arcoreitem);
    }

    @Override
    public void removeARCoreAccessory(Accessory accessory) {
        View v = mRelativeLayout.findViewById(getViewIdFromAccessory(accessory));
        if (v != null) {
            mRelativeLayout.removeView(v);
        }
    }

    @Override
    public void removeARCoreAccessories() {
        mRelativeLayout.removeAllViews();
    }

    @Override
    public void hideStartDemoButtonVisibility() {
        mFloatingActionButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tracker_selectaccessory_button:
                notifySelectAccessoryClicked();
                break;
            default:
                break;
        }
    }

    private int getViewIdFromAccessory(Accessory accessory) {
        return Integer.valueOf(accessory.getMac().replace(":", "").substring(8, 12), 16);
    }
}
