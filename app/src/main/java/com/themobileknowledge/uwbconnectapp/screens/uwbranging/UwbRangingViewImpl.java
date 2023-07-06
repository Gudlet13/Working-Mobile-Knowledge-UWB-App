package com.themobileknowledge.uwbconnectapp.screens.uwbranging;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.model.Position;
import com.themobileknowledge.uwbconnectapp.screens.common.ViewFactory;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;

public class UwbRangingViewImpl extends BaseObservableView<UwbRangingView.Listener> implements UwbRangingView, View.OnClickListener {

    private TextView selectedAccessoryText;
    private ImageView selectedAccessoryArrow;
    private ImageView selectedAccessoryCircle;

    public UwbRangingViewImpl(LayoutInflater inflater, ViewGroup parent, ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_uwbranging, parent, false));
        findViewById(R.id.uwbranging_selectaccessory_button).setOnClickListener(this);
        selectedAccessoryText = findViewById(R.id.uwbranging_seelectedaccessory_text);
        selectedAccessoryArrow = findViewById(R.id.uwbranging_seelectedaccessory_arrow);
        selectedAccessoryCircle = findViewById(R.id.uwbranging_seelectedaccessory_circle);
    }

    private void notifySelectAccessoryClicked() {
        for (Listener listener : getListeners()) {
            listener.onSelectAccessoryButtonClicked();
        }
    }

    private void notifySelectedAccessoryRemoveClicked() {
        for (Listener listener : getListeners()) {
            listener.onSelectedAccessoryRemoveClicked();
        }
    }

    private void notifySelectedAccessorySelectAccessoryClicked() {
        for (Listener listener : getListeners()) {
            listener.onSelectedAccessorySelectAccessoryClicked();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.uwbranging_selectaccessory_button:
                notifySelectAccessoryClicked();
                break;
            case R.id.uwbranging_selectedaccessory_remove:
                notifySelectedAccessoryRemoveClicked();
                break;
            case R.id.uwbranging_selectedaccessory_selectaccessory:
                notifySelectedAccessorySelectAccessoryClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public void showSelectAccessoryText() {
        findViewById(R.id.uwbranging_selectaccessory_text).setVisibility(View.VISIBLE);
        findViewById(R.id.uwbranging_selectaccessory_button).setVisibility(View.VISIBLE);
        findViewById(R.id.uwbranging_selectedaccessory_frame).setVisibility(View.GONE);
    }

    @Override
    public void showSelectedAccessory(Accessory accessory) {
        findViewById(R.id.uwbranging_selectaccessory_text).setVisibility(View.GONE);
        findViewById(R.id.uwbranging_selectaccessory_button).setVisibility(View.GONE);
        findViewById(R.id.uwbranging_selectedaccessory_frame).setVisibility(View.VISIBLE);
        findViewById(R.id.uwbranging_selectedaccessoryposition_frame).setVisibility(View.GONE);
        findViewById(R.id.uwbranging_selectedaccessory_remove).setOnClickListener(this);
        findViewById(R.id.uwbranging_selectedaccessory_selectaccessory).setOnClickListener(this);

        ((TextView) findViewById(R.id.uwbranging_selectedaccessory_address)).setText(accessory.getMac());
        if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
            ((TextView) findViewById(R.id.uwbranging_selectedaccessory_name)).setText(accessory.getAlias());
        } else {
            ((TextView) findViewById(R.id.uwbranging_selectedaccessory_name)).setText(accessory.getName());
        }
    }

    @Override
    public void updateSelectedAccessoryPosition(Accessory accessory, Position position) {
        findViewById(R.id.uwbranging_selectedaccessoryposition_frame).setVisibility(View.VISIBLE);

        double scaleFactor;
        if (position.getDistance() < 8.0) {
            scaleFactor = 1 - (position.getDistance() * 0.1);
        } else {
            scaleFactor = 0.2;
        }

        selectedAccessoryText.setVisibility(View.VISIBLE);
        selectedAccessoryArrow.setVisibility(View.VISIBLE);
        selectedAccessoryCircle.setVisibility(View.VISIBLE);

        // Show the scaled drawable
        Drawable scaledDrawable = getScaledDrawable(R.drawable.arrow, scaleFactor);
        if (scaledDrawable != null) {
            selectedAccessoryArrow.setImageDrawable(scaledDrawable);
        }

        if (position.getAzimuth() >= -10 && position.getAzimuth() <= 10) {
            if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distanceahead,
                        accessory.getAlias(),
                        (int) (position.getDistance() * 100),
                        (int) position.getAzimuth()));
            } else {
                selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distanceahead,
                        accessory.getName(),
                        (int) (position.getDistance() * 100),
                        (int) position.getAzimuth()));
            }
        } else {
            if (position.getAzimuth() >= 0) {
                if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                    selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distancedirection,
                            accessory.getAlias(),
                            (int) (position.getDistance() * 100),
                            getString(R.string.uwbranging_selectedaccessory_distancedirection_right),
                            (int) position.getAzimuth()));
                } else {
                    selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distancedirection,
                            accessory.getName(),
                            (int) (position.getDistance() * 100),
                            getString(R.string.uwbranging_selectedaccessory_distancedirection_right),
                            (int) position.getAzimuth()));
                }
            } else {
                if (accessory.getAlias() != null && !accessory.getAlias().isEmpty()) {
                    selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distancedirection,
                            accessory.getAlias(),
                            (int) (position.getDistance() * 100),
                            getString(R.string.uwbranging_selectedaccessory_distancedirection_left),
                            (int) position.getAzimuth()));
                } else {
                    selectedAccessoryText.setText(getString(R.string.uwbranging_selectedaccessory_distancedirection,
                            accessory.getName(),
                            (int) (position.getDistance() * 100),
                            getString(R.string.uwbranging_selectedaccessory_distancedirection_left),
                            (int) position.getAzimuth()));
                }
            }
        }

        // Rotate arrow and icon to show the direction
        selectedAccessoryArrow.setRotation(position.getAzimuth());
        selectedAccessoryCircle.setRotation(position.getAzimuth());
    }

    @Override
    public void clearAccessoryPosition() {
        selectedAccessoryText.setVisibility(View.GONE);
        selectedAccessoryArrow.setVisibility(View.GONE);
        selectedAccessoryCircle.setVisibility(View.GONE);
    }

    private Drawable getScaledDrawable(int drawableRes, double scaleFactor) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), drawableRes);
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return new BitmapDrawable(getContext().getResources(),
                    Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scaleFactor), (int) (bitmap.getHeight() * scaleFactor), true));
        }

        return null;
    }
}
