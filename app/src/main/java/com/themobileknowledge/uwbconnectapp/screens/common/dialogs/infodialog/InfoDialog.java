package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.infodialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;

public class InfoDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_BUTTON_CAPTION = "ARG_BUTTON_CAPTION";

    public static InfoDialog newInfoDialog(String title, String message, String buttonCaption) {
        InfoDialog infoDialog = new InfoDialog();
        Bundle args = new Bundle(3);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_BUTTON_CAPTION, buttonCaption);
        infoDialog.setArguments(args);
        return infoDialog;
    }

    private TextView mDialogTitle;
    private TextView mDialogMessage;
    private AppCompatButton mDialogBtnPositive;

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new IllegalStateException("arguments mustn't be null");
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.dialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.dialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.dialog_btn_positive);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_BUTTON_CAPTION));

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClicked();
            }
        });

        return dialog;
    }

    protected void onButtonClicked() {
        dismiss();
    }

}