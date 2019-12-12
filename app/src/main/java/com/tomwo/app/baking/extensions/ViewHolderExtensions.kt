package com.tomwo.app.baking.extensions

import com.tomwo.app.baking.App
import org.jetbrains.anko.ctx

fun getString(id: Int, order: Int): String = App.instance.ctx.resources.getString(id, order)
