package com.tomwo.app.baking.extensions

import android.os.Build


/**
 * Created by wooldridgetm on 2/17/18.
 */
inline fun supportOreo26( code: () -> Unit ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        code()
    }
}