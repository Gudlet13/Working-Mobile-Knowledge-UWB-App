package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.promptdialog;

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

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

public class PromptDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    public PromptDialog() {
    }

    public static PromptDialog newPromptDialog(String title, String message, String positiveButtonCaption) {
        return newPromptDialog(title, message, positiveButtonCaption, null);
    }

    public static PromptDialog newPromptDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        PromptDialog promptDialog = new PromptDialog();
        Bundle args = new Bundle(4);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);

        if(negativeButtonCaption != null && !negativeButtonCaption.isEmpty()) {
            args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButtonCaption);
        }

        promptDialog.setArguments(args);
        return promptDialog;
    }

    private TextView mDialogTitle;
    private TextView mDialogMessage;
    private AppCompatButton mDialogBtnPositive;
    private AppCompatButton mDialogBtnNegative;
    private DialogsEventBus mDialogsEventBus;

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
        dialog.setContentView(R.layout.dialog_prompt);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.dialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.dialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.dialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.dialog_btn_negative);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveButtonClicked();
            }
        });

        if(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION) != null && !getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION).isEmpty()) {
            mDialogBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

            mDialogBtnNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNegativeButtonClicked();
                }
            });
        } else {
            mDialogBtnNegative.setVisibility(View.INVISIBLE);
        }

        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new PromptDialogEvent(
                PromptDialogEvent.Button.CANCELLED
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new PromptDialogEvent(
                PromptDialogEvent.Button.POSITIVE
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new PromptDialogEvent(
                PromptDialogEvent.Button.NEGATIVE
        ));
    }

}