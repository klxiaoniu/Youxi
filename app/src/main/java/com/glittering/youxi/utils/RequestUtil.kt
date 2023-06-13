package com.glittering.youxi.utils

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody

class RequestUtil {
    companion object {
        fun generateJson(data: Any): RequestBody {
            return FormBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(data)
            )
        }
    }
}