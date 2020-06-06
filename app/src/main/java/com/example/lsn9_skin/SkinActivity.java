package com.example.lsn9_skin;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.lsn9_skin.skin.Skin;
import com.example.lsn9_skin.skin.SkinUtils;
import com.example.skin_core.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SkinActivity extends Activity {
    private static final String TAG = "lily/SkinActivity";
    /**
     * 从服务器拉取的皮肤表
     */
    List<Skin> skins = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);

        skins.add(new Skin("e0893ca73a972d82bcfc3a5a7a83666d", "1111111.skin", "app-skin-debug" +
                ".apk"));
    }

    /**
     * 下载皮肤包
     */
    private void selectSkin(Skin skin) {
        File theme = new File(getFilesDir(), "theme");
        if (theme.exists() && theme.isFile()) {
            theme.delete();
        }
        theme.mkdirs();
        File skinFile = skin.getSkinFile(theme);
        if (skinFile.exists()) {
            Log.d(TAG, "皮肤已存在,开始换肤");
            return;
        }
        Log.d(TAG, "皮肤不存在,开始下载");
        FileOutputStream fos = null;
        InputStream is = null;
        //临时文件
        File tempSkin = new File(skinFile.getParentFile(), skin.name + ".temp");
        Log.d(TAG, "selectSkin: "+tempSkin.getAbsolutePath());
        try {
            fos = new FileOutputStream(tempSkin);
            //假设下载皮肤包
            is = getAssets().open(skin.url);
            byte[] bytes = new byte[10240];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            //下载成功，将皮肤包信息insert已下载数据库
            Log.d(TAG, "皮肤包下载完成开始校验");
            //皮肤包的md5校验 防止下载文件损坏(但是会减慢速度。从数据库查询已下载皮肤表数据库中保留md5字段)
            if (TextUtils.equals(SkinUtils.getSkinMD5(tempSkin), skin.md5)) {
                Log.d(TAG, "校验成功,修改文件名。");
                tempSkin.renameTo(skinFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tempSkin.delete();
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void change(View view) {
        //使用第0个皮肤
        Skin skin = skins.get(0);
        Log.d(TAG, "change: get skin "+ skin.toString());
        selectSkin(skin);
        //换肤
        SkinManager.getInstance().loadSkin(skin.path);
    }

    public void restore(View view) {
        SkinManager.getInstance().loadSkin(null);
    }
}
