package com.glittering.youxi.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Base64
import java.io.ByteArrayOutputStream

class DrawableUtil {
    companion object {
        @Synchronized
        fun base64ToDrawable(icon: String): Drawable {
            val img: ByteArray = Base64.decode(icon.toByteArray(), Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
            return BitmapDrawable(bitmap)
        }

        @Synchronized
        fun drawableToBase64(drawable: Drawable?): String? {
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
                val baos = ByteArrayOutputStream(size)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val imagedata: ByteArray = baos.toByteArray()
                return Base64.encodeToString(imagedata, Base64.DEFAULT)
            }
            return null
        }

        @Synchronized
        fun bitmapToBase64(bitmap: Bitmap): String? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        @Synchronized
        fun bitmapShrinkToBase64(bitmap: Bitmap): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val format =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.JPEG
            bitmap.compress(format, 50, byteArrayOutputStream)
            var img = byteArrayOutputStream.toByteArray()
            while (img.size > 1024000) {
                val bitmap = BitmapFactory.decodeByteArray(img, 0, img.size)
                val resized = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * 0.8).toInt(),
                    (bitmap.height * 0.8).toInt(),
                    true
                )
                val stream = ByteArrayOutputStream()
                resized.compress(format, 50, stream)
                img = stream.toByteArray()
            }
            return Base64.encodeToString(img, Base64.DEFAULT)
        }

    }
}