package com.tomwo.app.baking

import android.app.Application
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tomwo.app.baking.extensions.DelegatesExt

/**
 * Created by wooldridgetm on 9/16/17.
 */
class App : Application() {
    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()
    }

    override fun onCreate()
    {
        super.onCreate()
        instance = this
    }


    // exoPlayer stuff
    private val userAgent : String by lazy { Util.getUserAgent(this, "ExoPlayerDemo") }

    fun buildHttpDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter) = DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)
    fun buildDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter) = DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter))
}
