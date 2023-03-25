package com.glittering.youxi.ui

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.dylanc.viewbinding.base.ActivityBinding
import com.dylanc.viewbinding.base.ActivityBindingDelegate
import com.gyf.immersionbar.ImmersionBar


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(),
    ActivityBinding<VB> by ActivityBindingDelegate() {

    private var mLoading: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithBinding()
//        val actionBar: ActionBar? = supportActionBar
//        if (actionBar != null) {
//            actionBar.setHomeButtonEnabled(true)
//            actionBar.setDisplayHomeAsUpEnabled(true)
//        }


//        val color = SurfaceColors.SURFACE_2.getColor(this)
//        window.statusBarColor = color // Set color of system statusBar same as ActionBar
//        window.navigationBarColor =
//            color // Set color of system navigationBar same as BottomNavigationView

        ImmersionBar.with(this)
            .transparentBar()
            .statusBarDarkFont(!isDarkTheme(this))   //状态栏字体是深色，不写默认为亮色
            .navigationBarDarkIcon(true)
            .init()

    }

    fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> finish() // back button
//        }
//        return super.onOptionsItemSelected(item)
//    }

    fun showLoading() {
//        val view = layoutInflater.inflate(R.layout.dialog_loading_pbar, null)
//        mLoading = MaterialAlertDialogBuilder(this).setView(view).setCancelable(false).show()
    }

    fun dismissLoading() {
        mLoading?.dismiss()
    }
}