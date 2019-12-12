package com.tomwo.app.baking.ui.player

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.tomwo.app.baking.domain.Direction
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.warn
import java.io.File
import java.lang.ref.WeakReference

class PlayerManager(context: Context,
                    name: String,
                    data: List<Direction>,
                    private val trackChangeHandler:(Int?) -> Unit): IPLayerManager
{
    companion object {
        private const val TAG = "PlayerManager"
        private const val CACHE_SIZE_BYTES: Long = 100 * 1024 * 1024
        private val BANDWIDTH_METER = DefaultBandwidthMeter()
    }

    var initialized: Boolean = false
    var playWhenReady: Boolean = false
        set(value) {
            field = value
            player?.playWhenReady = field
        }

    var currentTrack: Int = 0
        get() = player?.currentWindowIndex ?: field

    override val data: List<Direction>
    override val mediaDataSourceFactory: DataSource.Factory
    override var maxWindow: Int = 0

    private val cache: Cache

    // SimpleExoPlayer
    private var player: SimpleExoPlayer? = null
    private var playerEvtListener: PlayerEventListener? = null
    private var playerViewWR: WeakReference<PlayerView>? = null

    private var currentWindow: Int = 0
    private var contentPosition: Long = 0

    init {
        val dataSource = DefaultDataSourceFactory(context, BANDWIDTH_METER,
                DefaultHttpDataSourceFactory(
                        Util.getUserAgent(context, "Baking"),
                        BANDWIDTH_METER))

        val f = File(context.cacheDir, name)

        cache = SimpleCache(f, LeastRecentlyUsedCacheEvictor(CACHE_SIZE_BYTES))
        mediaDataSourceFactory = CacheDataSourceFactory(cache, dataSource)
        this.data = data
    }

    fun init(context: Context, playerView: PlayerView)
    {
        if (initialized) return
        initialized = true

        // TODO: inject via Dagger
        // create he Player
        this.player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(context),
                DefaultTrackSelector( AdaptiveTrackSelection.Factory(BANDWIDTH_METER) ),
                DefaultLoadControl())

        val player = this.player!!

        // set the Player the view will interact with
        playerView.player = player
        playerView.controllerHideOnTouch = true
        this.playerViewWR = WeakReference(playerView)

        // create an EventListener for the Player
        this.playerEvtListener = PlayerEventListener(this.playerViewWR!!, trackChangeHandler)

        // add the Listener to the instance of SimpleExoPlayer
        player.addListener(this.playerEvtListener!!)
    }

    fun reloadSource() {
        if (player?.playbackState != Player.STATE_READY) {
            player?.let {
                it.prepare( buildMediaSource() )
                it.seekTo(currentWindow, contentPosition)
                it.playWhenReady = this.playWhenReady
            }
        }
    }

    fun seekTo(windowIndex: Int, contentPosition: Long = this.contentPosition) {
        val max = if (windowIndex < 0) 0 else if (windowIndex > maxWindow) maxWindow else windowIndex

        val player = this.player!!
        if (player.currentWindowIndex == max && player.contentPosition == contentPosition) return

        player.seekTo(max, contentPosition)
    }

    fun reset() {
        if (player == null) return

        currentWindow   = player!!.currentWindowIndex
        contentPosition = player!!.contentPosition

        release()
    }

    fun release() {
        if (player == null) return
        player!!.removeListener( playerEvtListener )
        player!!.release()
        player = null
        playerEvtListener = null
        initialized = false
    }



    /**
     * PlayerEventListener
     */
    private class PlayerEventListener(private val playerView: WeakReference<PlayerView>,
                                      private val trackChangeHandler:(Int?) -> Unit): Player.DefaultEventListener(),
                                                                                          AnkoLogger
    {
        companion object {
            private const val TAG = "PlayerEventListener"
            private const val NO_MEDIA = "no media source prepared"
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray)
        {
            debug("new track! selections=$trackSelections, trackGroups=$trackGroups")

            Log.i(TAG, "trackGroups    =$trackGroups")
            Log.i(TAG, "trackSelections=$trackSelections")

            println("windowIndex       =${this.playerView.get()?.player?.currentWindowIndex}")
            println("currentPeriodIndex=${this.playerView.get()?.player?.currentPeriodIndex}")

            println("stop")
            trackChangeHandler( this.playerView.get()?.player?.currentWindowIndex )
        }

        override fun onLoadingChanged(isLoading: Boolean)
        {
            debug("Fx onLoadingChanged($isLoading)")
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int)
        {
           debug( "Fx onPlayerStateChanged($playWhenReady, $playbackState)")

            val state = when(playbackState) {
                Player.STATE_BUFFERING ->  "STATE_BUFFERING"
                Player.STATE_READY ->  "STATE_READY"
                Player.STATE_ENDED ->  "STATE_ENDED"
                else ->  NO_MEDIA  // Player.STATE_IDLE, happens when offline
            }

           debug( state )

            if (state == NO_MEDIA)
            {
                playerView.get()?.hideController()
            }
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?)
        {
            debug( "Fx onPlaybackParametersChanged($playbackParameters)")
        }

        override fun onPlayerError(error: ExoPlaybackException)
        {
            warn("Fx onPlayerError($error)", error.sourceException)
        }
    }
}