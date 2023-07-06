package com.themobileknowledge.uwbconnectapp.screens.common.dialogs.selectaccessoriesdialog;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.themobileknowledge.uwbconnectapp.R;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.BaseDialog;
import com.themobileknowledge.uwbconnectapp.screens.common.dialogs.DialogsEventBus;

import java.util.ArrayList;
import java.util.List;

public class SelectAccessoriesDialog extends BaseDialog {

    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_CAPTION = "ARG_POSITIVE_BUTTON_CAPTION";
    protected static final String ARG_NEGATIVE_BUTTON_CAPTION = "ARG_NEGATIVE_BUTTON_CAPTION";

    private SelectAccessoriesDialog() {
    }

    public static SelectAccessoriesDialog newSelectAccessoriesDialog(String title, String message, String positiveButtonCaption, String negativeButtonCaption) {
        SelectAccessoriesDialog promptDialog = new SelectAccessoriesDialog();
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
    private RecyclerView mRecyclerView;
    private SelectAccessoriesRecyclerAdapter mSelectAccessoriesRecyclerAdapter;
    private List<SelectAccessoriesDialogItem> mAccessoriesList = new ArrayList<>();
    private int mMaxAllowedAccessories;

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
        dialog.setContentView(R.layout.dialog_selectaccessories);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogTitle = dialog.findViewById(R.id.selectaccessoriesdialog_txt_title);
        mDialogMessage = dialog.findViewById(R.id.selectaccessoriesdialog_txt_message);
        mDialogBtnPositive = dialog.findViewById(R.id.selectaccessoriesdialog_btn_positive);
        mDialogBtnNegative = dialog.findViewById(R.id.selectaccessoriesdialog_btn_negative);
        mRecyclerView = dialog.findViewById(R.id.selectaccessoriesdialog_recycler);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectAccessoriesRecyclerAdapter = new SelectAccessoriesRecyclerAdapter(getCompositionRoot().getToastHelper(), getCompositionRoot().getBaseViewFactory());
        mRecyclerView.setAdapter(mSelectAccessoriesRecyclerAdapter);

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

        mSelectAccessoriesRecyclerAdapter.setMaxAllowedAccessories(mMaxAllowedAccessories);
        mSelectAccessoriesRecyclerAdapter.bindAccessoriesList(mAccessoriesList);
        mSelectAccessoriesRecyclerAdapter.bindListener(new SelectAccessoriesRecyclerAdapter.Listener() {
            @Override
            public void onAccessoryEditClicked(int position) {
                dismiss();
                mDialogsEventBus.postEvent(new SelectAccessoriesDialogEvent(
                        SelectAccessoriesDialogEvent.Button.EDIT,
                        mSelectAccessoriesRecyclerAdapter.getSelectedPositions(),
                        mSelectAccessoriesRecyclerAdapter.getEditPosition()
                ));
            }
        });

        return dialog;
    }

    public void setMaxAllowedAccessories(int maxAllowedAccessories) {
        mMaxAllowedAccessories = maxAllowedAccessories;
    }

    public void bindAccessoriesList(List<SelectAccessoriesDialogItem> accessoriesList) {
        mAccessoriesList = accessoriesList;
    }

    public void refresh() {
        if (mSelectAccessoriesRecyclerAdapter != null) {
            if (mAccessoriesList.size() != 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
            }

            mSelectAccessoriesRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        mDialogsEventBus.postEvent(new SelectAccessoriesDialogEvent(
                SelectAccessoriesDialogEvent.Button.CANCEL
        ));
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new SelectAccessoriesDialogEvent(
                SelectAccessoriesDialogEvent.Button.SELECT,
                mSelectAccessoriesRecyclerAdapter.getSelectedPositions(),
                mSelectAccessoriesRecyclerAdapter.getEditPosition()
        ));
    }

    protected void onNegativeButtonClicked() {
        dismiss();
        mDialogsEventBus.postEvent(new SelectAccessoriesDialogEvent(
                SelectAccessoriesDialogEvent.Button.CANCEL
        ));
    }
}