package com.example.skin_core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    private static final String TAG = "lily/SkinLayoutFactory";
    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private SkinAttribute skinAttribute;
    private Activity activity;

    private  static final Map<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class,AttributeSet.class};
    public SkinLayoutInflaterFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        this.skinAttribute = new SkinAttribute(typeface);
    }

    /**
     * 创建对应布局并返回
     * @param parent    当前TAG 父布局
     * @param name      在布局中的TAG 如 TextView, android.support.v7.widget.Toolbar
     * @param context   上下文
     * @param attrs     对应布局TAG中的属性 如: android:text android:src
     * @return view     null则由系统创建
     */
    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

//        换肤就是在需求时候替换View的属性，所以在这里创建View 从而修改View属性
        View view = createViewFromTag(name,context,attrs);
        // 自定义View
        if (null ==view){
            view = createView(name,context,attrs);
        }
        if (null != view) {

            Log.e(TAG, "onCreateView: " + String.format("检查[%s]:" + name, context.getClass().getSimpleName()));
//            加载属性
            skinAttribute.load(view, attrs);

        }else {
            Log.e(TAG, "onCreateView: view  =  null " );
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attrs) {

        Constructor< ? extends  View> constructor = findConstructor(context,name);
        View view= null;
        if (constructor != null) {
            try {

                view = constructor.newInstance(context, attrs);

                return view;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);

        if (null == constructor){
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                if (constructor != null){
                    mConstructorMap.put(name,constructor);
                    Log.d(TAG, "findConstructor: name ="+ name+ " constructor "+constructor.getName());

                }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return constructor;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
//        Log.d(TAG, "createViewFromTag: name = "+name);
//        如果包含 . 则不是SDK中的View ，可能是自定义View
        if (-1 != name.indexOf('.')){
            return null;
        }
        View view = null;
        for (int i = 0; i <mClassPrefixList.length ; i++) {
            view= createView(mClassPrefixList[i]+name,context,attrs);
            if (null != view ){
                return view;
            }
        }

        Log.e(TAG, "createViewFromTag: get view  = null");
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//        Log.d(TAG, "onCreateView: name,context,attrs");
        return null;
    }



    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "Observer update:change skin");
        SkinThemeUtils.updateStatusBarColor(activity);
        Log.d(TAG, "updateStatusBarColor ");
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        if (typeface !=null) {
            Log.d(TAG, "skinAttribute setTypeface and applySkin");
            skinAttribute.setTypeface(typeface);
            skinAttribute.applySkin();
        }
    }
}
