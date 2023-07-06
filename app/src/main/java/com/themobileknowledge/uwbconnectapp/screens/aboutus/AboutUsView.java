package com.themobileknowledge.uwbconnectapp.screens.aboutus;

import com.themobileknowledge.uwbconnectapp.screens.common.views.IBaseObservableView;

public interface AboutUsView extends IBaseObservableView<AboutUsView.Listener> {

    interface Listener {
        void onBackPressed();

        void onMkUwbConnectAppClicked();

        void onMkVisitUsLinkClicked();

        void onNxpTrimensionLinkClicked();

        void onMkContactEmailClicked();

        void onMkTwitterIconClicked();

        void onMkLinkedinIconClicked();
    }

    void showMkUwbConnectApp(String version);

    void showMkVisitUsLink();

    void showNxpTrimensionLink();

    void showMkContactEmail();

}
