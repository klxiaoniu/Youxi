package com.glittering.youxi.utils

import com.glittering.youxi.R

class DialogUtil {
    companion object {
        fun stylize(dialog: androidx.appcompat.app.AlertDialog) {
            val btnPositive = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            val btnNegative = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
            btnPositive.setBackgroundColor(applicationContext.resources.getColor(R.color.primary_yellow, null))
            btnPositive.setTextColor(applicationContext.resources.getColor(R.color.black, null))
            btnNegative.setBackgroundColor(applicationContext.resources.getColor(R.color.primary_yellow_2, null))
            btnNegative.setTextColor(applicationContext.resources.getColor(R.color.black, null))
            btnPositive.setPadding(80, 50, 80, 50)
            btnNegative.setPadding(80, 50, 80, 50)
            btnPositive.textSize = 18f
            btnNegative.textSize = 18f
        }
    }
}