package com.armscat.photoeditors.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by parkin on 2016/11/22.
 */
public class ScannerUtils {
    // 扫描的三种方式
    public static enum ScannerType {
        RECEIVER, MEDIA
    }

    //保存图片
    public static void saveImageToGallery(final Context context, File oldFile,ScannerType type) {
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "retaium");
        if (!appDir.exists()) {
            // 目录不存在 则创建
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".png";
        final File mSaveFile = new File(appDir, fileName);

        if (oldFile.exists()) {

            FileOutputStream fs = null;
            InputStream inputStream = null;
            //存入图片文件
            try {
                if (!mSaveFile.exists()) {
                    mSaveFile.createNewFile();
                }
                inputStream = new FileInputStream(oldFile);
                fs = new FileOutputStream(mSaveFile, true);
                byte[] buffer = new byte[1024 * 1024 * 10];

                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fs.write(buffer, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "图片保存为" + mSaveFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                });

                if (type == ScannerType.RECEIVER) {
                    ScannerByReceiver(context, mSaveFile.getAbsolutePath());
                } else if (type == ScannerType.MEDIA) {
                    ScannerByMedia(context, mSaveFile.getAbsolutePath());
                }
            }
        }
    }

    /** Receiver扫描更新图库图片 **/

    private static void ScannerByReceiver(Context context, String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                + path)));
        Log.v("TAG", "receiver scanner completed");
    }

    /** MediaScanner 扫描更新图片图片 **/

    private static void ScannerByMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[] {path}, null, null);
        Log.v("TAG", "media scanner completed");
    }
}
