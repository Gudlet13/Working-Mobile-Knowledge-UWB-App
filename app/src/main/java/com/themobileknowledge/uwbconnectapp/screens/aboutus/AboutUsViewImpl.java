package com.themobileknowledge.uwbconnectapp.screens.aboutus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.views.BaseObservableView;

public class AboutUsViewImpl extends BaseObservableView<AboutUsView.Listener> implements AboutUsView, View.OnClickListener {

    private ConstraintLayout appLayout;
    private ConstraintLayout visitMkLayout;
    private ConstraintLayout visitNxpTrimensionLayout;
    private ConstraintLayout contactMkEmailLayout;

    public AboutUsViewImpl(LayoutInflater inflater, ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_aboutus, parent, false));
        findViewById(R.id.aboutus_twitter_icon).setOnClickListener(this);
        findViewById(R.id.aboutus_linkedin_icon).setOnClickListener(this);

        appLayout = findViewById(R.id.aboutus_item_app);
        visitMkLayout = findViewById(R.id.aboutus_item_visitmk);
        visitNxpTrimensionLayout = findViewById(R.id.aboutus_item_visitnxptrimension);
        contactMkEmailLayout = findViewById(R.id.aboutus_item_contactmkemail);

        appLayout.setOnClickListener(this);
        visitMkLayout.setOnClickListener(this);
        visitNxpTrimensionLayout.setOnClickListener(this);
        contactMkEmailLayout.setOnClickListener(this);
    }

    private void notifyMkVisitUsLinkClicked() {
        for (Listener listener : getListeners()) {
            listener.onMkVisitUsLinkClicked();
        }
    }

    private void notifyTwitterIconClicked() {
        for (Listener listener : getListeners()) {
            listener.onMkTwitterIconClicked();
        }
    }

    private void notifyLinkedinIconClicked() {
        for (Listener listener : getListeners()) {
            listener.onMkLinkedinIconClicked();
        }
    }

    private void notifyNxpTrimensionLinkClicked() {
        for (Listener listener : getListeners()) {
            listener.onNxpTrimensionLinkClicked();
        }
    }

    private void notifyMkContactEmailClicked() {
        for (Listener listener : getListeners()) {
            listener.onMkContactEmailClicked();
        }
    }

    private void notifyMkUwbConnectAppClicked() {
        for (Listener listener : getListeners()) {
            listener.onMkUwbConnectAppClicked();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aboutus_item_app:
                notifyMkUwbConnectAppClicked();
                break;
            case R.id.aboutus_item_visitmk:
                notifyMkVisitUsLinkClicked();
                break;
            case R.id.aboutus_item_visitnxptrimension:
                notifyNxpTrimensionLinkClicked();
                break;
            case R.id.aboutus_item_contactmkemail:
                notifyMkContactEmailClicked();
                break;
            case R.id.aboutus_twitter_icon:
                notifyTwitterIconClicked();
                break;
            case R.id.aboutus_linkedin_icon:
                notifyLinkedinIconClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public void showMkUwbConnectApp(String version) {
        ((TextView) (appLayout.findViewById(R.id.aboutus_item_title))).setText(
                String.format(getString(R.string.aboutus_item_app_option)));
        ((TextView) (appLayout.findViewById(R.id.aboutus_item_value))).setText(
                getString(R.string.app_version, version));
    }

    @Override
    public void showMkVisitUsLink() {
        ((TextView) (visitMkLayout.findViewById(R.id.aboutus_item_title))).setText(
                String.format(getString(R.string.aboutus_item_visitmk_option)));
        ((TextView) (visitMkLayout.findViewById(R.id.aboutus_item_value))).setText(
                getString(R.string.mk_website));
    }

    @Override
    public void showNxpTrimensionLink() {
        ((TextView) (visitNxpTrimensionLayout.findViewById(R.id.aboutus_item_title))).setText(
                String.format(getString(R.string.aboutus_item_visitnxptrimension_option)));
        ((TextView) (visitNxpTrimensionLayout.findViewById(R.id.aboutus_item_value))).setText(
                getString(R.string.nxp_trimension_website));
    }

    @Override
    public void showMkContactEmail() {
        ((TextView) (contactMkEmailLayout.findViewById(R.id.aboutus_item_title))).setText(
                String.format(getString(R.string.aboutus_item_contactmkemail_option)));
        ((TextView) (contactMkEmailLayout.findViewById(R.id.aboutus_item_value))).setText(
                getString(R.string.mk_contact_email));
    }
}
