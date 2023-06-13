package com.glittering.youxi.ui.activity

import android.os.Bundle
import com.glittering.youxi.R
import com.glittering.youxi.databinding.ActivityPayResultBinding

class PayResultActivity : BaseActivity<ActivityPayResultBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_slide_in_bottom,0)
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.anim_slide_out_bottom)
    }
}