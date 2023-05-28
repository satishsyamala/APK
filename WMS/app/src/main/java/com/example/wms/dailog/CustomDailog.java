package com.example.wms.dailog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

public class CustomDailog extends DialogFragment {

    private View customrView;
    private CustomDailogIntf listener;
    private String title;
    private String message;
    private int actionId;

    public CustomDailog(View customrView) {
        this.customrView = customrView;
        if (customrView != null) {
            if (this.customrView.getParent() != null)
                ((ViewGroup) this.customrView.getParent()).removeView(this.customrView);
        }
    }

    public CustomDailog(View customrView, CustomDailogIntf listener) {
        this.customrView = customrView;
        this.listener = listener;
        if (customrView != null) {
            if (this.customrView.getParent() != null)
                ((ViewGroup) this.customrView.getParent()).removeView(this.customrView);
        }
    }

    public CustomDailog(View customrView, CustomDailogIntf listener, String title, String message) {
        this.customrView = customrView;
        this.listener = listener;
        this.title = title;
        this.message = message;
        if (customrView != null) {
            if (this.customrView.getParent() != null)
                ((ViewGroup) this.customrView.getParent()).removeView(this.customrView);
        }
    }

    public CustomDailog(View customrView, CustomDailogIntf listener, String title, String message, int actionId) {
        this.customrView = customrView;
        this.listener = listener;
        this.title = title;
        this.message = message;
        this.actionId = actionId;
        if (customrView != null) {
            if (this.customrView.getParent() != null)
                ((ViewGroup) this.customrView.getParent()).removeView(this.customrView);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            // Get the layout inflater
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            if (this.title != null)
                builder.setTitle(this.title);
            if (this.message != null)
                builder.setMessage(this.message);
            // Add action buttons
            if (this.customrView != null) {
                builder.setView(this.customrView);
            }
            if(actionId==-2) {
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        listener.onDialogNegativeClick(CustomDailog.this, actionId);
                    }
                });
            }else
            if(actionId==-1) {
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(CustomDailog.this, actionId);
                    }
                });
            }else{
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("Hi ");
                                listener.onDialogPositiveClick(CustomDailog.this, actionId);
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                listener.onDialogNegativeClick(CustomDailog.this, actionId);
                            }
                        });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.create();
    }
}
