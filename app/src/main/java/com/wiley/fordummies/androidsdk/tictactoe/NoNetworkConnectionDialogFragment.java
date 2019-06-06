package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class NoNetworkConnectionDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.warning))
                .setMessage(getResources().getString(R.string.no_net_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.ok_text),
                        (dialog, which) -> {
                        }).create();
    }
}
