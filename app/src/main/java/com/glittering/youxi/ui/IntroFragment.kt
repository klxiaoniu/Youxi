package com.glittering.youxi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.glittering.youxi.R

class IntroFragment(val i: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        view.findViewById<ImageView>(R.id.imageView).setImageResource(
            when (i) {
                0 -> R.drawable.error
                1 -> R.drawable.close
                2 -> R.drawable.loading
                else -> R.drawable.error
            }
        )
        return view
    }
}