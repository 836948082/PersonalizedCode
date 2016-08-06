package com.runtai.personalizedcode.uitls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片加载工具类
 */
public abstract class ToolImage {
	public static File file;

	/**
	 * 先保存到本地再广播到图库
	 * */
	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(),
				"code");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = "qrcode.jpg";
		file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
			// 最后通知图库更新
			context.sendBroadcast(new Intent(
					Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
							+ file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getTime(){
		long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date d1 = new Date(time);
		String t1 = format.format(d1);
		Log.e("msg", t1);
	}



}
