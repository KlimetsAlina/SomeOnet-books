package com.example.fix.books.rest.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.fix.books.BuildConfig;
import com.example.fix.books.R;


public class DialogWaitManager {

    private final static String TAG = "DialogWaitManager";
    private final static boolean D = BuildConfig.DEBUG;

    private static DialogWaitManager dialogWaitManagerInstance;

    private DialogWaitManager() {
    }

    public static DialogWaitManager getInstance() {
        if (dialogWaitManagerInstance == null) {
            dialogWaitManagerInstance = new DialogWaitManager();
        }
        return dialogWaitManagerInstance;
    }

    private static Dialog mDialog;

    public void showDialog(Context context) {
        if (mDialog == null || !mDialog.isShowing()) {
            mDialog = new Dialog(context);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Transparent dialog background
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // Transparent window background
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_wait);
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            }
            catch (IllegalArgumentException ex){
                if(D) Log.e(TAG, ex.getMessage());
            }
            mDialog = null;
        }
    }
}
