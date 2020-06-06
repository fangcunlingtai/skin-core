package com.example.skin_core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SkinPreference {
    private static final String SKIN_SHARED = "skins";
    private static final String KEY_SKIN_PATH = "skin-path";
    private static final String TAG = "lily/SkinPreference";
    private static SkinPreference instance;
    private final SharedPreferences mPref;

    SkinPreference(Context context) {
        this.mPref = context.getSharedPreferences(SKIN_SHARED,Context.MODE_PRIVATE);

    }


    public static void init(Context mContext) {
        if (instance == null){
            synchronized (SkinPreference.class){
                if (instance == null){
                    instance = new SkinPreference(mContext.getApplicationContext());
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return instance;
    }

    public String getSkin() {
        return mPref.getString(KEY_SKIN_PATH,null);
    }
    public void setSkin(String skinPath){
        Log.d(TAG, "setSkin: save skinPath "+skinPath);
        mPref.edit().putString(KEY_SKIN_PATH,skinPath).apply();
    }


}
