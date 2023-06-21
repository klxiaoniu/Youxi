package com.glittering.youxi.ui.delegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dylanc.loadingstateview.LoadingStateView
import com.dylanc.loadingstateview.ViewType
import com.glittering.youxi.R

class ErrorViewDelegate : LoadingStateView.ViewDelegate(ViewType.ERROR) {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup): View =
        inflater.inflate(R.layout.layout_error, parent, false)
}