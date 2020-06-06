package com.example.lsn9_skin.skin;

import androidx.annotation.NonNull;

import java.io.File;

public class Skin {
    public String path = "";
    public String url = "";
    public String name ="";
    public CharSequence md5 ="";
    public File file;
    public Skin(CharSequence md5, String name,String url ) {
        this.url = url;
        this.name = name;
        this.md5 = md5;
    }

    public File getSkinFile(File theme) {
        if (null == file) {
            file = new File(theme, name);
        }
        path = file.getAbsolutePath();
        return file;
    }

    @Override
    public String toString() {
        return "Skin{" +
                "path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", md5=" + md5 +
                ", file=" + file +
                '}';
    }
}
