package com.example.skin_core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skin_core.utils.SkinResources;
import com.example.skin_core.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

class SkinAttribute {
    private static final List<String> mAttributes = new ArrayList<>();
    private static final String TAG = "lily/SkinAttribute";;

    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
        mAttributes.add("skinTypeface");
    }

    private Typeface typeface;
//    记录换肤需要操作的View 与属性信息
    private List<SkinView>  mSkinViews = new ArrayList<>();

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void applySkin() {
        Log.d(TAG, "SkinAttribute : applySkin()  mSkinViews count "+ mSkinViews.size());
        for(SkinView skinView:mSkinViews){
            skinView.applySkin(typeface);
        }
    }

    public void load(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPars = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
//            获得属性名 mAttributes
            String attributeName = attrs.getAttributeName(i);

            if (mAttributes.contains(attributeName)){
                String attributeValue = attrs.getAttributeValue(i);
//                如果是颜色值 #开头表示写死的颜色 不可改
                if (attributeValue.startsWith("#")){
                    continue;
                }
                int resId ;
//                ? 开头表示android 系统属性
                if (attributeValue.startsWith("?")){
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(),new int[]{attrId})[0];
                }else {
//            正常以 @ 开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }
                Log.d(TAG, "load: "+"   " + attributeName + " = " + attributeValue);
                SkinPair skinPair = new SkinPair(attributeName,resId);
                mSkinPars.add(skinPair);
            }
        }

        if (!mSkinPars.isEmpty() || view instanceof  TextView || view instanceof SkinViewSupport){
//            没有属性满足 但是需要修改字体
            
            SkinView skinView = new SkinView(view,mSkinPars);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);

        }

    }

    static class SkinView {
        private static final String TAG = "lily/SkinView";
        View view ;
        List<SkinPair> skinPairs = new ArrayList<>();

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(Typeface typeface) {
            applyTypeface(typeface);
            applySkinSupport();
            for (SkinPair skinPair : skinPairs){
                Drawable left = null,top = null,right = null,bottom=null;
                Object background;
                Log.d(TAG, "  applySkin: skinPair.attributeName  "+skinPair.attributeName+" for the view "+view);
                switch (skinPair.attributeName){
                    case "background":
                         background = SkinResources.getInstance().getBackground(skinPair.resId);
//                       Color
                        if (background instanceof Integer){

                            view.setBackgroundColor((Integer) background);
//                            Log.d(TAG, "applySkin: setBackgroundColor");
                        }else {// drawable
                            view.setBackground((Drawable) background);
//                            Log.d(TAG, "applySkin: setBackgroundDrawable");
                        }

                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer){// 颜色图片
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer) background));
//                            Log.d(TAG, "applySkin: setImageDrawable ColorDrawable");
                        }else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
//                            Log.d(TAG, "applySkin: setImageDrawable Drawable");
                        }
//                        Log.d(TAG, "applySkin: view "+ view+" set src");
                        break;
                    case "textColor":
                        ((TextView)view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
//                        Log.d(TAG, "applySkin:  set textColor");
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
//                        Log.d(TAG, "applySkin: view "+ view+" set drawableRight");
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
//                        Log.d(TAG, "applySkin: view "+ view+" set drawableTop");
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
//                        Log.d(TAG, "applySkin: view "+ view+" set drawableLeft");
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
//                        Log.d(TAG, "applySkin: view "+ view+" set drawableBottom");
                        break;
                    case "skinTypeface"://自定义属性
                        applyTypeface(SkinResources.getInstance().getTypeface(skinPair.resId));
//                        Log.d(TAG, "applySkin: view "+ view+" set skinTypeface");
                        break;
                    default:
                        break;
                }
                if (null != left || null!= right || null != top || null != bottom){
                    ((TextView)view).setCompoundDrawablesWithIntrinsicBounds(left,top,right,bottom);
//                    Log.d(TAG, "applySkin: left,top,right,bottom");
                }
            }

        }

        private void applySkinSupport() {
            if(view instanceof SkinViewSupport){
                ((SkinViewSupport) view).applySkin();
                Log.d(TAG, "applySkinSupport: applySkin()");
            }
        }
//      文本改变字体样式
        private void applyTypeface(Typeface typeface) {
            if (view instanceof TextView){
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    private static class SkinPair {
        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
