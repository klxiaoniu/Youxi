package com.glittering.youxi.utils

import android.util.Log
import com.glittering.youxi.R
import com.hjq.toast.Toaster


fun ToastSuccess(msg: CharSequence) {
    Toaster.setView(R.layout.toast_success)
    Toaster.show(msg)
    //Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    Log.d("ToastShort", msg.toString())
}

fun ToastFail(msg: CharSequence) {
    Toaster.setView(R.layout.toast_fail)
    Toaster.show(msg)
    //Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    Log.d("ToastLong", msg.toString())
    //throw Exception("TTTT")
}
