package com.glittering.youxi.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.preference.PreferenceManager
import com.glittering.youxi.R

class DarkUtil {
    companion object {
        fun isDarkTheme(context: Context): Boolean {
            return if (isFollowSystem(context)) {
                val flag = context.resources.configuration.uiMode and
                        Configuration.UI_MODE_NIGHT_MASK
                flag == Configuration.UI_MODE_NIGHT_YES
            } else {
                isForceDark(context)
            }
        }

        fun isFollowSystem(context: Context): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString("skin", "0") == "0"
        }

        fun isForceDark(context: Context): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString("skin", "0") == "1"
        }

        fun reverseColorIfDark(list: List<ImageView>) {
            if (isDarkTheme(applicationContext)) {
                list.forEach { view ->
                    view.setColorFilter(applicationContext.getColor(R.color.white))
                }
            }
        }

        fun addMaskIfDark(viewGroup: ViewGroup) {
            if (isDarkTheme(applicationContext)) {
                val nightViewParam = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                            or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    PixelFormat.TRANSPARENT
                )
                nightViewParam.width = ViewGroup.LayoutParams.MATCH_PARENT
                nightViewParam.height = ViewGroup.LayoutParams.MATCH_PARENT
                nightViewParam.gravity = Gravity.CENTER
                val nightView = View(applicationContext)
                nightView.setBackgroundColor(0x66000000)
                viewGroup.addView(nightView, nightViewParam)
            }
        }
    }
}