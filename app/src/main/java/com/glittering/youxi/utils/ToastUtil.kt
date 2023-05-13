package com.glittering.youxi.utils

import android.util.Log
import com.glittering.youxi.R
import com.hjq.toast.Toaster


fun ToastSuccess(msg: CharSequence) {
    Toaster.setView(R.layout.toast_success)
    Toaster.show(msg)
}

fun ToastFail(msg: CharSequence) {
    Toaster.setView(R.layout.toast_fail)
    Toaster.show(msg)
}

fun ToastInfo(msg: CharSequence) {
    Toaster.setView(R.layout.toast_info)
    Toaster.show(msg)
}