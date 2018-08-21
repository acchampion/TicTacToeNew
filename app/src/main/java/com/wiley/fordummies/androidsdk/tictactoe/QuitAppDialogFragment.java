package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Objects;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class QuitAppDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setTitle(Objects.requireNonNull(activity).getResources().getString(R.string.exit))
                .setMessage(Objects.requireNonNull(activity).getResources().getString(R.string.should_quit))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(Objects.requireNonNull(activity).getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setNegativeButton(Objects.requireNonNull(activity).getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }
}
