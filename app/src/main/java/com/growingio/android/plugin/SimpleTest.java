package com.growingio.android.plugin;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;


/**
 * <p>
 *
 * @author cpacm 2022/3/30
 */
class SimpleTest extends Dialog implements DialogInterface, View.OnClickListener {
    public SimpleTest(@NonNull Context context) {
        super(context);

        WebView wb = new WebView(context);
        wb.loadUrl("https://www.baidu.com");
    }

    @Override
    public void onClick(View v) {
        Log.d("test","SimpleTest");
    }
}
