package com.platypusinnovations.smsbutler.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.platypusinnovations.smsbutler.R;

public class AlertUtil {

    public static ProgressDialog createProgressSpinner(Context context, String title, String message) {
        ProgressDialog dialog = null;
        if (dialog == null) {
            dialog = ProgressDialog.show(context, title, message);
        } else {
            dialog.setTitle(title);
            dialog.setMessage(message);
        }
        dialog.show();

        return dialog;
    }

    public static AlertDialog createModalAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.alert_dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }


}
