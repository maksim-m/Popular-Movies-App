package me.maxdev.popularmoviesapp.ui.movies;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.PreferencesUtility;

public class SortingDialogFragment extends DialogFragment {

    public static final String TAG = "SortingDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(getString(R.string.pref_sort_by_label));
        builder.setNegativeButton(getString(R.string.action_cancel), null);
        builder.setSingleChoiceItems(
                R.array.pref_sort_by_labels,
                PreferencesUtility.getSortByPreferenceIndex(getActivity()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesUtility.saveSortByPreference(getActivity(), which);
                        // TODO: update movies
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
