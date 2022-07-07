package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.wiley.fordummies.androidsdk.tictactoe.R;

import java.util.Objects;

/**
 * Created by adamcchampion on 2017/08/12.
 */

public class QuitAppDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = requireActivity();
        return new AlertDialog.Builder(activity)
                .setTitle(Objects.requireNonNull(activity).getResources().getString(R.string.exit))
                .setMessage(Objects.requireNonNull(activity).getResources().getString(R.string.should_quit))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(Objects.requireNonNull(activity).getResources().getString(R.string.yes),
						(dialogInterface, i) -> System.exit(0))
                .setNegativeButton(Objects.requireNonNull(activity).getString(R.string.no),
                        (dialog, which) -> {
                        }).create();
    }
}
