package com.model.cjx.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjx on 2017/1/12.
 */
public class Tools {
    private static String cachPath;

    /**
     * 获取缓存文件保存的路径
     *
     * @param c
     * @return
     */
    public synchronized static String getCachPath(Context c) {
        if (cachPath == null) {
            cachPath = getDiskCacheDir(c) + "/";
        }
        File f = new File(cachPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return cachPath;
    }

    private static String tempPath;

    public synchronized static String getTempPath(Context c) {
        if (tempPath == null) {
            tempPath = getCachPath(c) + "temp/";
        }
        File f = new File(tempPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return tempPath;
    }

    private static String getDiskCacheDir(Context context) {
        if (context == null) {
            return null;
        }
        boolean hasExternal = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        File cacheDir;
        if (hasExternal) {
            cacheDir = context.getExternalCacheDir();
        } else {
            cacheDir = context.getCacheDir();
        }
        if (cacheDir == null) {
            return null;
        }
        return cacheDir.getAbsolutePath();
    }

    private static String expcetionPath;

    public synchronized static String getExceptionPath(Context c) {
        if (expcetionPath == null) {
            expcetionPath = getCachPath(c) + "exception/";
        }
        File f = new File(expcetionPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        return expcetionPath;
    }

    public static boolean compressImage(Bitmap src, String savePath, int quality) {
        boolean result = false;
        if (src != null) {
            FileOutputStream fos = null;
            try {
                File f = new File(savePath);
                fos = new FileOutputStream(f);
                result = src.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                src.recycle();
            }
        }
        return result;
    }

    // 返回服务器图片完整路径
    public static void setImage(Activity context, ImageView imageView, String path, int errorRes) {
        if (context.isFinishing()) {
            return;
        }
        if (!TextUtils.isEmpty(path)) {
            if(errorRes > 0){
                Glide.with(context).load(path).error(errorRes).into(imageView);
            }else{
                Glide.with(context).load(path).into(imageView);
            }

        } else {
            imageView.setImageBitmap(null);
        }
    }

    // 返回服务器图片完整路径
    public static void setHeadImage(final Activity context, final ImageView imageView, String path, int errorRes) {
        if (context.isFinishing()) {
            return;
        }
        if (!TextUtils.isEmpty(path)) {
            Glide.with(context).load(path).asBitmap().centerCrop().error(errorRes).into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            imageView.setImageResource(errorRes);
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

    public static void saveToFile(Context context, String filename, String content) {
        File meterCache = new File(getExceptionPath(context), filename + ".txt");
        try {
            FileOutputStream fout = new FileOutputStream(meterCache, true);
            byte[] bytes = content.getBytes();
            fout.write("\n\r\n".getBytes());
            fout.write(("=========== " + sdf.format(new Date()) + "============").getBytes());
            fout.write("\n\r\n".getBytes());
            fout.write(bytes);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPhone(String mobiles) {
        Pattern p = Pattern
                .compile("1([3-8])\\d{9}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isUrl(String url) {
        Pattern p = Pattern
                .compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?");
        Matcher m = p.matcher(url);
        return m.matches();
    }
}
