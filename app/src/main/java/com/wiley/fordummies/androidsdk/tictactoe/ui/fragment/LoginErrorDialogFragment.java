package com.wiley.fordummies.androidsdk.tictactoe.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.wiley.fordummies.androidsdk.tictactoe.R;

/**
 * DialogFragment that gives user login error.
 *
 * Created by adamcchampion on 2017/08/04.
 */

public class LoginErrorDialogFragment extends DialogFragment {
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireActivity())
                .setTitle(getResources().getString(R.string.error))
                .setMessage(getResources().getString(R.string.login_error_text))
                .setPositiveButton(getResources().getString(R.string.ok_text),
                        (dialog, which) -> {
                        }).create();
    }
}
