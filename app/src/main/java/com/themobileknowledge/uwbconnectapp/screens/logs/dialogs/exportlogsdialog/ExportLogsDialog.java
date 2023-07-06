package com.themobileknowledge.uwbconnectapp.screens.logs.dialogs.exportlogsdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

import java.util.ArrayList;
import java.util.List;

public class ExportLogsDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    // Initialize all formats to false (checkbox not selected)
    private final List<Boolean> selectedExportFormats = new ArrayList<Boolean>() {
        {
            add(false);
            add(false);
        }
    };

    private ExportLogsDialog() {
    }

    public static ExportLogsDialog newExportLogsDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        ExportLogsDialog promptDialog = new ExportLogsDialog();
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
    private AppCompatCheckBox mDialogCSVCheckbox;
    private AppCompatCheckBox mDialogTXTCheckbox;

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
        dialog.setContentView(R.layout.dialog_exportlogs);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.exportlogsdialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.exportlogsdialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.exportlogsdialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.exportlogsdialog_btn_negative);
        mDialogCSVCheckbox = dialog.findViewById(R.id.exportlogsdialog_csv_checkbox);
        mDialogTXTCheckbox = dialog.findViewById(R.id.exportlogsdialog_txt_checkbox);

        mDialogTitle.setText(getArguments().getString(ARG_TITLE));
        mDialogMessage.setText(getArguments().getString(ARG_MESSAGE));
        mDialogBtnPositive.setText(getArguments().getString(ARG_POSITIVE_BUTTON_CAPTION));
        mDialogBtnNegative.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_CAPTION));

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

        mDialogTXTCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedExportFormats.set(0, isChecked);
            }
        });

        mDialogCSVCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedExportFormats.set(1, isChecked);
            }
        });

        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new ExportLogsDialogEvent(
                ExportLogsDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new ExportLogsDialogEvent(
                ExportLogsDialogEvent.Button.EXPORT,
                selectedExportFormats
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new ExportLogsDialogEvent(
                ExportLogsDialogEvent.Button.CANCEL
        ));
    }

}