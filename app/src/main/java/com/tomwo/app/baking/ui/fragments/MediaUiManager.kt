package com.tomwo.app.baking.ui.fragments

import android.graphics.drawable.AnimatedVectorDrawable
import android.util.Log
import android.widget.ImageView
import com.tomwo.app.baking.ui.player.PlayerManager

interface MediaUiManager
{
    var playerManager: PlayerManager
    var playPause: ImageView
    var prev: ImageView
    var next: ImageView

    var pauseToPlay: AnimatedVectorDrawable
    var playToPause: AnimatedVectorDrawable

    fun initMediaUi()
    {
        this.playPause.setOnClickListener {

            val playWhenReady = !playerManager.playWhenReady
            playerManager.playWhenReady = playWhenReady

            val drawable: AnimatedVectorDrawable = if (playWhenReady) playToPause else pauseToPlay
            playPause.setImageDrawable(drawable)
            drawable.start()

            if (playWhenReady) this.onMediaClick()
        }

        this.prev.setOnClickListener {
            playerManager.seekTo(playerManager.currentTrack - 1, 0)
            this.onMediaClick()
        }
        this.next.setOnClickListener {
            playerManager.seekTo(playerManager.currentTrack + 1, 0)
            this.onMediaClick()
        }
    }

    /**
     * occurs when Play, Prev, or Next buttons are clicked
     */
    fun onMediaClick() {
        Log.d("MediaUiManager","Fx onMediaClick()")
    }


}