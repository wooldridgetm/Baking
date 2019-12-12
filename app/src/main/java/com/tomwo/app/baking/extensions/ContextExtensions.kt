package com.tomwo.app.baking.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import com.tomwo.app.baking.utils.NetworkUtils

fun Context.drawable(res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.color(res: Int): Int = ContextCompat.getColor(this, res)

fun Context.offline(): Boolean = !NetworkUtils.isConnected(this)