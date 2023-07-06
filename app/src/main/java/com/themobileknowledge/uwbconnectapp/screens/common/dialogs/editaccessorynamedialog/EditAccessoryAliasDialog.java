package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.editaccessorynamedialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.model.Accessory;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

public class EditAccessoryAliasDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    private EditAccessoryAliasDialog() {
    }

    public static EditAccessoryAliasDialog newEditAccessoryAliasDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        EditAccessoryAliasDialog promptDialog = new EditAccessoryAliasDialog();
        Bundle args = new Bundle(4);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);
        args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButtonCaption);
        promptDialog.setArguments(args);
        return promptDialog;
    }

    private TextView mDialogTitle;
    private TextView mDialogMessage;
    private AppCompatButton mDialogBtnPositive;
    private AppCompatButton mDialogBtnNegative;
    private DialogsEventBus mDialogsEventBus;

    private Accessory mAccessory;
    private String mAccessoryAlias;

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
        dialog.setContentView(R.layout.dialog_editaccessoryalias);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.editaccessoryaliasdialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.editaccessoryaliasdialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.editaccessoryaliasdialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.editaccessoryaliasdialog_btn_negative);

        ((TextView) dialog.findViewById(R.id.editaccessoryaliasdialog_name_value)).setText(mAccessory.getName());
        ((TextView) dialog.findViewById(R.id.editaccessoryaliasdialog_mac_value)).setText(mAccessory.getMac());

        if (mAccessory.getAlias() != null && !mAccessory.getAlias().isEmpty()) {
            ((EditText) dialog.findViewById(R.id.editaccessoryaliasdialog_alias_value)).setText(mAccessory.getAlias());
        }

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));
        mDialogBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccessoryAlias = ((EditText) dialog.findViewById(R.id.editaccessoryaliasdialog_alias_value)).getText().toString();
                onPositiveButtonClicked();
            }
        });

        mDialogBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNegativeButtonClicked();
            }
        });

        return dialog;
    }

    public void bindAccessory(Accessory accessory) {
        this.mAccessory = accessory;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new EditAccessoryAliasDialogEvent(
                EditAccessoryAliasDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new EditAccessoryAliasDialogEvent(
                EditAccessoryAliasDialogEvent.Button.EDIT,
                mAccessory,
                mAccessoryAlias
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new EditAccessoryAliasDialogEvent(
                EditAccessoryAliasDialogEvent.Button.CANCEL
        ));
    }
}