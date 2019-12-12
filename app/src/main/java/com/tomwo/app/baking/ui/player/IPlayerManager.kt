package com.tomwo.app.baking.ui.player

import android.net.Uri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.tomwo.app.baking.domain.Direction

interface IPLayerManager
{
    val data: List<Direction>
    val mediaDataSourceFactory: DataSource.Factory
    var maxWindow: Int

    fun buildMediaSource(): MediaSource
    {
        val uriLinks = arrayOfNulls<Uri>(data.size)

        var urlNum = 0
        for (dir in data)
        {
            if (dir.isVideo)
            {
                uriLinks[urlNum] = dir.uri
                ++urlNum
            }
        }

        val uris = arrayOfNulls<Uri>(urlNum)
        System.arraycopy(uriLinks, 0, uris, 0, urlNum)

        val mediaSources = arrayOfNulls<MediaSource>(uris.size)

        uris.indices.takeWhile { uris[it] != null }
                .forEach {
                    mediaSources[it] = ExtractorMediaSource.Factory(this.mediaDataSourceFactory).createMediaSource( uris[it], null, null)
                }

        this.maxWindow = uris.size - 1
        return if (mediaSources.size == 1) mediaSources[0]!! else ConcatenatingMediaSource(*mediaSources)
    }
}