package com.example.skin_core.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class SkinResources {
    private static final String TAG = "lily/SkinResources";
    private static SkinResources instance;
//    更新的皮肤资源
    private Resources mSkinResource;
//    更新的皮肤资源的包
    private String mSkinPkgName;
//    默认皮肤
    private boolean isDefaultSkin = true;
//    默认皮肤资源
    private Resources mAppResources;
    private Context context;

    public SkinResources(Context mContext) {

        mAppResources = mContext.getResources();
        context = mContext;
    }

    public static void init(Context mContext) {
        if (instance == null){
            synchronized (SkinResources.class){
                if(instance == null){
                    instance = new SkinResources(mContext);
                }
            }
        }
    }
    public static SkinResources getInstance(){
        return instance;
    }

    public void reset() {
        mSkinResource = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    public void applySkin(Resources skinResources, String packageName) {
        mSkinResource = skinResources;
        mSkinPkgName = packageName;
//        是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(packageName) || skinResources ==null;
        Log.d(TAG, "applySkin: isDefaultSkin "+ isDefaultSkin);
    }
//      获取字体
    public Typeface getTypeface(int skinTypefaceId) {
        String skinTypefacePath = getString(skinTypefaceId);
        if (TextUtils.isEmpty(skinTypefacePath)){
            return Typeface.DEFAULT;
        }

        try{
            if (isDefaultSkin){
                return Typeface.createFromAsset(mAppResources.getAssets(),skinTypefacePath);

            }
            return Typeface.createFromAsset(mSkinResource.getAssets(),skinTypefacePath);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return Typeface.DEFAULT;
    }

    private String getString(int resId) {
       try {
           if (isDefaultSkin){// 原皮肤
               return  mAppResources.getString(resId);
           }

           int skinId = getIdentifier(resId);
           if (skinId == 0){// 原皮肤
               return mAppResources.getString(resId);
           }
//        更新皮肤
           return mSkinResource.getString(skinId);
       }catch (Resources.NotFoundException e){
           e.printStackTrace();
       }
       return null;

    }

    private int getIdentifier(int resId) {
        if (isDefaultSkin){
            return resId;
        }
        //    获取对应id 在当前的名称 colorPrimary 在皮肤包中不一定就是当前程序的id
        String resName = mAppResources.getResourceName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        int skinId = mSkinResource.getIdentifier(resName,resType,mSkinPkgName);
        return skinId;



    }

    public int getColor(int colorResId) {
        if (isDefaultSkin){
            return mAppResources.getColor(colorResId);
        }
        int skinId = getIdentifier(colorResId);
        if (skinId == 0){
            return mAppResources.getColor(colorResId);

        }
        return mSkinResource.getColor(skinId);
    }

    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);
        if (resourceTypeName.equals("color")){
            return getColor(resId);
        }else {
            return getDrawable(resId);
        }
    }

    public Drawable getDrawable(int resId) {
        if(isDefaultSkin){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mAppResources.getDrawable(resId,null);
            }else {
               return ContextCompat.getDrawable(context,resId);
            }

        }
        int skinId = getIdentifier(resId);
        if (skinId == 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return mAppResources.getDrawable(resId,null);
            }else {
              return   ContextCompat.getDrawable(context,resId);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mSkinResource.getDrawable(skinId,null);
        }else {
           return ContextCompat.getDrawable(context,resId);
        }

    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin){
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId ==0){
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResource.getColorStateList(resId);
    }


}
