package com.glittering.youxi.ui.dialog

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.widget.ImageView
import android.widget.TextView
import com.glittering.youxi.R
import kotlin.properties.Delegates

class BottomPayDialog(context: Context, price: Double) : CustomBottomDialog(context) {
    private var price by Delegates.notNull<Double>()

    init {
        this.price = price
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_pay)
        super.onCreate(savedInstanceState)
        val text = "￥$price"
        val textSpan = SpannableStringBuilder(text)
        textSpan.setSpan(
            AbsoluteSizeSpan(40),
            0,
            1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        textSpan.setSpan(
            AbsoluteSizeSpan(70),
            1,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        findViewById<TextView>(R.id.tv_price).text = textSpan
        findViewById<ImageView>(R.id.iv_close).setOnClickListener {
            dismiss()
        }
    }
}