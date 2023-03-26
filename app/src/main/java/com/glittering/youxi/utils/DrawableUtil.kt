package com.glittering.youxi.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream

class DrawableUtil {

    @Synchronized
    fun byteToDrawable(icon: String): Drawable? {
        val img: ByteArray = Base64.decode(icon.toByteArray(), Base64.DEFAULT)
        val bitmap: Bitmap
        if (img != null) {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            return BitmapDrawable(bitmap)
        }
        return null
    }

    @Synchronized
    fun drawableToByte(drawable: Drawable?): String? {
        if (drawable != null) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(
                0, 0, drawable.intrinsicWidth,
                drawable.intrinsicHeight
            )
            drawable.draw(canvas)
            val size = bitmap.width * bitmap.height * 4

// 创建一个字节数组输出流,流的大小为size
            val baos = ByteArrayOutputStream(size)

// 设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

// 将字节数组输出流转化为字节数组byte[]
            val imagedata: ByteArray = baos.toByteArray()
            return Base64.encodeToString(imagedata, Base64.DEFAULT)
        }
        return null
    }

}