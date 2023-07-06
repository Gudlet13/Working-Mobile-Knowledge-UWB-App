package com.themobileknowledge.uwbconnectapp.screens.settings.dialogs.selectsettingsdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

import java.util.List;

public class SelectSettingsDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    private SelectSettingsDialog() {
    }

    public static SelectSettingsDialog newSelectSettingsDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        SelectSettingsDialog promptDialog = new SelectSettingsDialog();
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
    private RadioGroup mRadioGroup;
    private DialogsEventBus mDialogsEventBus;

    private List<String> mOptionsList;
    private int mSelectedPosition;

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
        dialog.setContentView(R.layout.dialog_selectsettings);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.selectsettingsdialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.selectsettingsdialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.selectsettingsdialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.selectsettingsdialog_btn_negative);
        mRadioGroup = dialog.findViewById(R.id.selectsettingsdialog_radiogroup);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));
        mDialogBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mSelectedPosition = checkedId;
            }
        });

        mDialogBtnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveButtonClicked();
            }
        });

        mDialogBtnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNegativeButtonClicked();
            }
        });

        // Create the RadioGroup from the passed list options
        int index = 0;
        for (String option : mOptionsList) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);
            radioButton.setId(index);
            mRadioGroup.addView(radioButton);
            index++;
        }

        // Check selected option
        ((RadioButton) mRadioGroup.getChildAt(mSelectedPosition)).setChecked(true);

        return dialog;
    }

    public void bindOptionsList(List<String> optionsList) {
        mOptionsList = optionsList;
    }

    public void setSelectedOption(int selectedOption) {
        mSelectedPosition = selectedOption;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new SelectSettingsDialogEvent(
                SelectSettingsDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new SelectSettingsDialogEvent(
                SelectSettingsDialogEvent.Button.SELECT,
                mSelectedPosition
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new SelectSettingsDialogEvent(
                SelectSettingsDialogEvent.Button.CANCEL
        ));
    }

}