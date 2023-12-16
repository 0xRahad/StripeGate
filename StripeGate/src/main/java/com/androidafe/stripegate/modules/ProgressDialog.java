package com.androidafe.stripegate.modules;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.androidafe.stripegate.R;

public class ProgressDialog {

    Dialog dialog;

    public void showDialog(Context context){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
