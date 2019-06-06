package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by adamcchampion on 2017/08/16.
 */

public class ContactPermissionDeniedDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.read_contacts_permission_denied))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.ok_text),
                        (dialog, which) -> {
                            Activity activity = getActivity();

                            if (activity != null) {
                                activity.finish();
                            }
                        }).create();
    }

}
