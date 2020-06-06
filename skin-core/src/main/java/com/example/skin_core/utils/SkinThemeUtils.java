package com.example.skin_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.skin_core.R;

public class SkinThemeUtils {
//  status bar
    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            R.attr.colorPrimaryDark
    };
//    status bar  navigation bar
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor, android.R.attr
            .navigationBarColor};

    private static int[] TYPEFACE_ATTRS = {R.attr.skinTypeface};

    public static int[] getResId(Context context, int[] attrs){
        int[] resIds = new int[attrs.length];
        TypedArray array = context.obtainStyledAttributes(attrs);
        for (int i = 0; i <attrs.length ; i++) {
            resIds[i] = array.getResourceId(i,0);

        }
        array.recycle();
        return resIds;
    }

    public static Typeface getSkinTypeface(Activity activity) {
        int skinTypefaceId = getResId(activity,TYPEFACE_ATTRS)[0];
        return SkinResources.getInstance().getTypeface(skinTypefaceId);

    }


    public static void updateStatusBarColor(Activity activity) {
        //5.0以上才能修改
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        int[] resIds = getResId(activity,STATUSBAR_COLOR_ATTRS);
//        没有配置 属性获取为0
        if (resIds[0] == 0){
           int getResId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if (getResId != 0){
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(getResId));
            }
        }else {
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(resIds[0]));
        }
//        修改底部虚拟按键的颜色
        if (resIds[1] != 0){
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(resIds[1]));
        }

    }
}
