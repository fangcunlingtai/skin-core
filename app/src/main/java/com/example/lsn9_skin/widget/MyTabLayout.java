package com.example.lsn9_skin.widget;

import android.content.Context;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;


import com.example.lsn9_skin.R;
import com.example.lsn9_skin.SkinViewSupport;
import com.example.skin_core.utils.SkinResources;
import com.google.android.material.tabs.TabLayout;

public class MyTabLayout extends TabLayout implements SkinViewSupport {

    int tabIndicatorColorResId;
    int tabTextColorResId;

    public MyTabLayout(Context context) {
        this(context,null,0);
    }

    public MyTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public MyTabLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, 0);
        tabIndicatorColorResId = typedArray.getResourceId(R.styleable.TabLayout_tabIndicatorColor,0);
        tabTextColorResId = typedArray.getResourceId(R.styleable.TabLayout_tabTextColor,0);
        typedArray.recycle();
    }

    @Override
    public void applySkin() {
        if (tabIndicatorColorResId != 0){
          int color=  SkinResources.getInstance().getColor(tabIndicatorColorResId);
            setSelectedTabIndicatorColor(color);
        }
        if (tabTextColorResId != 0){
            ColorStateList textColor = SkinResources.getInstance().getColorStateList(tabTextColorResId);
            setTabTextColors(textColor);
        }
    }


}
