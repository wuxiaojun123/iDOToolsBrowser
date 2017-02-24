package com.manga.country.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.manga.country.R;
import com.idotools.utils.LogUtils;

import java.io.File;

/**
 * Created by wuxiaojun on 16-12-22.
 */

public class ToastClickView {

    public static void show(final Context context, final String filePath) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast_look, null);
        TextView textView = (TextView) view.findViewById(R.id.id_tv_look);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                File file = new File(filePath);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                context.startActivity(intent);

                LogUtils.e("点击打开");
            }
        });
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void show(final Context context, final Uri uri) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast_look, null);
        TextView textView = (TextView) view.findViewById(R.id.id_tv_look);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                context.startActivity(intent);
            }
        });
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }


}
