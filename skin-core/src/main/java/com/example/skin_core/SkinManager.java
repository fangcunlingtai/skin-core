package com.example.skin_core;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.example.skin_core.utils.SkinPreference;
import com.example.skin_core.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

public class SkinManager extends Observable {

    private static final String TAG ="lily/SkinManager" ;
    private static SkinManager instance;
    private SkinActivityLifecycle mSkinActivityLifecycle;
    private Application mContext;


    public static void init(Application application){
        if (instance == null){
            synchronized (SkinManager.class){
                if (instance == null){
                    instance = new SkinManager(application);
                }
            }
        }
    }

    public static SkinManager getInstance(){
        return instance;
    }

    public SkinManager(Application application) {
        mContext = application;
//        共享首选项 用于记录当前使用的皮肤
        SkinPreference.init(mContext);
//        资源管理类 用于从 app 皮肤中加载资源
        SkinResources.init(mContext);
//        注册Activity生命周期
        mSkinActivityLifecycle = new SkinActivityLifecycle();
        mContext.registerActivityLifecycleCallbacks(mSkinActivityLifecycle);
//        加载皮肤
        loadSkin(SkinPreference.getInstance().getSkin());
    
    }
//  加载皮肤包 并更新
    public void loadSkin(String skinPath) {
        Log.d(TAG, "loadSkin: "+skinPath);
        if (TextUtils.isEmpty(skinPath)){
            SkinPreference.getInstance().setSkin("");
            SkinResources.getInstance().reset();
        }else {
            try {
                AssetManager assetManager = AssetManager.class.newInstance();
//                添加资源进入资源管理器
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.setAccessible(true);
                addAssetPath.invoke(assetManager,skinPath);
                Resources resources = mContext.getResources();
//                横竖 语言
                Resources skinResources = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
//                获取外部APK 皮肤包的包名
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                String packageName = packageArchiveInfo.packageName;
                Log.d(TAG, "loadSkin: package "+packageName);

                SkinResources.getInstance().applySkin(skinResources,packageName);
//               保存当前使用的皮肤包
                SkinPreference.getInstance().setSkin(skinPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        应用皮肤包
        Log.d(TAG, "loadSkin: setChanged ");
        setChanged();
//        通知观察者
        Log.d(TAG, "loadSkin: Observable notifyObservers");
        notifyObservers();

    }

    public void updateSkin(Activity activity) {
        Log.d(TAG, "updateSkin: "+activity.toString());
        mSkinActivityLifecycle.updateSkin(activity);
    }
}
