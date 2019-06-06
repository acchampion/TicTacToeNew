package com.wiley.fordummies.androidsdk.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * DialogFragment asking if user should abandon Tic-Tac-Toe game.
 * <p>
 * Created by adamcchampion on 2017/08/20.
 */

public class AbandonGameDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.exit))
                .setMessage(getResources().getString(R.string.abandon_game))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    Activity activity = getActivity();

                    if (activity != null) {
                        activity.finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                }).create();
    }
}
