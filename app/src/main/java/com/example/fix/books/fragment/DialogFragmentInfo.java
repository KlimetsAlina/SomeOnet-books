package com.example.fix.books.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.fix.books.R;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;


public class DialogFragmentInfo extends DialogFragment {

    public DialogFragmentInfo() {
    }

    //находим макет
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ChangeLogRecyclerView chgList = (ChangeLogRecyclerView) layoutInflater.inflate(R.layout.changelog_fragment_dialogmaterial, null);

        //АлертДайлог
        return new AlertDialog.Builder(getActivity(),
                R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.changelog_title_standarddialog)
                .setView(chgList)
                .setPositiveButton(R.string.about_ok,
                        (dialog, whichButton) -> {
                            dialog.dismiss();
                        }
                )
                .create();

    }
}
