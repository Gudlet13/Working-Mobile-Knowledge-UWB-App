package com.themobileknowledge.uwbconnectapp.screens.distancealert.dialogs.editdistancealertthresholdsdialog;

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
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

public class EditDistanceAlertThresholdsDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    private EditDistanceAlertThresholdsDialog() {
    }

    public static EditDistanceAlertThresholdsDialog newEditDistanceAlertThresholdsDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        EditDistanceAlertThresholdsDialog promptDialog = new EditDistanceAlertThresholdsDialog();
        Bundle args = new Bundle(4);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_CAPTION, positiveButtonCaption);
        args.putString(ARG_NEGATIVE_BUTTON_CAPTION, negativeButtonCaption);
        promptDialog.setArguments(args);
        return promptDialog;
    }

    private EditText mDialogCloseRangeThreshold;
    private EditText mDialogFarRangeThreshold;
    private TextView mDialogTitle;
    private TextView mDialogMessage;
    private AppCompatButton mDialogBtnPositive;
    private AppCompatButton mDialogBtnNegative;
    private DialogsEventBus mDialogsEventBus;

    private int mCloseRangeThreshold = -1;
    private int mFarRangeThreshold = -1;

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
        dialog.setContentView(R.layout.dialog_editdistancealertthresholds);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.editthresholdsdialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.editthresholdsdialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.editthresholdsdialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.editthresholdsdialog_btn_negative);
        mDialogCloseRangeThreshold = dialog.findViewById(R.id.editthresholdsdialog_closerange_value);
        mDialogFarRangeThreshold = dialog.findViewById(R.id.editthresholdsdialog_farrange_value);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));
        mDialogBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

        mDialogCloseRangeThreshold.setText(String.valueOf(mCloseRangeThreshold));
        mDialogFarRangeThreshold.setText(String.valueOf(mFarRangeThreshold));

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogCloseRangeThreshold.getText() != null && !mDialogCloseRangeThreshold.getText().toString().isEmpty()) {
                    try {
                        mCloseRangeThreshold = Integer.parseInt(mDialogCloseRangeThreshold.getText().toString());
                    } catch (NumberFormatException e) {
                        mCloseRangeThreshold = 0;
                    }
                } else {
                    mCloseRangeThreshold = 0;
                }

                if (mDialogFarRangeThreshold.getText() != null && !mDialogFarRangeThreshold.getText().toString().isEmpty()) {
                    try {
                        mFarRangeThreshold = Integer.parseInt(mDialogFarRangeThreshold.getText().toString());
                    } catch (NumberFormatException e) {
                        mFarRangeThreshold = 0;
                    }
                } else {
                    mFarRangeThreshold = 0;
                }

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

    public void bindThresholds(int closeRangeThreshold, int farRangeThreshold) {
        this.mCloseRangeThreshold = closeRangeThreshold;
        this.mFarRangeThreshold = farRangeThreshold;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new EditDistanceAlertThresholdsDialogEvent(
                EditDistanceAlertThresholdsDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new EditDistanceAlertThresholdsDialogEvent(
                EditDistanceAlertThresholdsDialogEvent.Button.EDIT,
                mCloseRangeThreshold,
                mFarRangeThreshold
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new EditDistanceAlertThresholdsDialogEvent(
                EditDistanceAlertThresholdsDialogEvent.Button.CANCEL
        ));
    }
}