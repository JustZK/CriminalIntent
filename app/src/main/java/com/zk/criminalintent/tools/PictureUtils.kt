package com.zk.criminalintent.tools

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import kotlin.math.roundToInt

/**
 * @author ZhuKun
 * @date 2021/12/16
 * @apiNote
 */
object PictureUtils {

    fun getScaledBitmap(path: String, activity: Activity): Bitmap {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return getScaledBitmap(path, size.x, size.y)
    }

    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
        // Read in the dimensions of the image on disk
        //读取磁盘上图像的尺寸
        var options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()
        // Figure out how much to scale down by
        // 弄清楚要缩小多少
        var inSampleSize = 1
        if (srcHeight > destHeight || srcWidth > destWidth) {
            val heightScale = srcHeight / destHeight;
            val widthScale = srcWidth / destWidth;
            inSampleSize = (if (heightScale > widthScale) heightScale else widthScale).roundToInt()
        }
        options = BitmapFactory.Options()
        options.inSampleSize = inSampleSize
        // Read in and create final bitmap
        // 读入并创建最终位图
        return BitmapFactory.decodeFile(path, options)
    }
}