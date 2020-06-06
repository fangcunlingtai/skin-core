package com.example.lsn9_skin.skin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;


public class SkinUtils {
    public static String getSkinMD5(File file) {
        FileInputStream fileInputStream = null;
        BigInteger bigInteger = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[10240];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1){
                MD5.update(buffer,0,length);
            }
            byte[] digest = MD5.digest();
            bigInteger = new BigInteger(1,digest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bigInteger.toString(16);
        }
    }
}
