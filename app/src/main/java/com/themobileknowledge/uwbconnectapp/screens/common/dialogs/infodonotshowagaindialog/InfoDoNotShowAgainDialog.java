package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodonotshowagaindialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

public class InfoDoNotShowAgainDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";

    private boolean showInfo = true;

    private InfoDoNotShowAgainDialog() {
    }

    public static InfoDoNotShowAgainDialog newInfoDoNotShowAgainDialog(String title, String message, String positiveButtonCaption) {
        InfoDoNotShowAgainDialog infoDoNotShowAgainDialog = new InfoDoNotShowAgainDialog();
        Bundle args = new Bundle(3);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);
        infoDoNotShowAgainDialog.setArguments(args);
        return infoDoNotShowAgainDialog;
    }

    private TextView mDialogTitle;
    private TextView mDialogMessage;
    private AppCompatButton mDialogBtnPositive;
    private DialogsEventBus mDialogsEventBus;
    private AppCompatCheckBox mCheckboxDoNotShowAgain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogsEventBus = getCompositionRoot().getDialogsEventBus();
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new IllegalStateException("arguments mustn't be null");
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_infodonotshowagain);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.infodonotshowagaindialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.infodonotshowagaindialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.infodonotshowagaindialog_btn_positive);
        mCheckboxDoNotShowAgain = dialog.findViewById(R.id.infodonotshowagaindialog_ckbox_donotshowagain);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveButtonClicked();
            }
        });

        mCheckboxDoNotShowAgain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showInfo = !isChecked;
        });

        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new InfoDoNotShowAgainDialogEvent(
                InfoDoNotShowAgainDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new InfoDoNotShowAgainDialogEvent(
                InfoDoNotShowAgainDialogEvent.Button.ACCEPT,
                showInfo
        ));
    }
}