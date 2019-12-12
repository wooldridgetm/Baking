package com.tomwo.app.baking.ui.activities

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.tomwo.app.baking.R

interface WinManager {

    fun initWindow(ctx: Context, window: Window, lightNav: Boolean, colorRes: Int = android.R.color.black) {
        if (ctx.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else
        {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(ctx, colorRes)
        }

        if (Build.VERSION.SDK_INT == 26 && lightNav)
        {
            // This attribute can only be set in code on API 26. It's in the theme in 27+.
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }
}