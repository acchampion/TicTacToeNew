package com.wiley.fordummies.androidsdk.tictactoe.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.wiley.fordummies.androidsdk.tictactoe.R;

/**
 * Created by adamcchampion on 2017/08/14.
 */

public class NoNetworkConnectionDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.warning))
                .setMessage(getResources().getString(R.string.no_net_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getResources().getString(R.string.ok_text),
                        (dialog, which) -> {
                        }).create();
    }
}
