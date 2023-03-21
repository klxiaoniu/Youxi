package com.xiaoniu.fund

import android.util.Log
import android.widget.Toast


fun ToastShort(msg: CharSequence) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    Log.d("ToastShort", msg.toString())
}

fun ToastLong(msg: CharSequence) {
    Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    Log.d("ToastLong", msg.toString())
    //throw Exception("TTTT")
}
