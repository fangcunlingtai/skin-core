package com.example.skin_core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.view.LayoutInflaterCompat;

import com.example.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Field;


class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "lily/Lifecycle";
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactoriesMap = new
            ArrayMap<>();

    private boolean ready =false;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

//        更新状态栏
        SkinThemeUtils.updateStatusBarColor(activity);
//        更新字体
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

//        更新布局视图
//        获得Activity 的布局加载器
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        /**
         * Android 布局加载器 使用 mFactorySet 标记上是否设置过Factory
         * 如设置过抛出异常一次
         * 设置 mFactorySet 为 False
         */
        if (ready ){
            return;
        }
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater,false);
            Log.d(TAG, "onActivityCreated: mFactorySet = "+field.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "mFactorySet get failed " );

        }

        ready = true;
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity,typeface);
        LayoutInflaterCompat.setFactory2(layoutInflater,skinLayoutInflaterFactory);
        mLayoutInflaterFactoriesMap.put(activity,skinLayoutInflaterFactory);
        SkinManager.getInstance().addObserver(skinLayoutInflaterFactory);
        Log.d(TAG, "onActivityCreated: ready setFactory2");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactoriesMap.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
        ready =false;

    }

    public void updateSkin(Activity activity) {
        if (mLayoutInflaterFactoriesMap.isEmpty()){
            Log.d(TAG, "updateSkin: mLayoutInflaterFactoriesMap.isEmpty()");
            return;
        }
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = mLayoutInflaterFactoriesMap.get(activity);
        if (skinLayoutInflaterFactory != null){
            Log.d(TAG, "updateSkin: skinLayoutInflaterFactory "+skinLayoutInflaterFactory.toString());
            skinLayoutInflaterFactory.update(null,null);
        }

    }
}
