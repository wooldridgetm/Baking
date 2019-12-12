package com.tomwo.app.baking.ui.fragments

import android.arch.lifecycle.Lifecycle
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.design.R.attr.alpha
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.exoplayer2.util.Util
import com.tomwo.app.baking.App
import com.tomwo.app.baking.domain.Direction
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.extensions.ctx
import com.tomwo.app.baking.extensions.drawable
import com.tomwo.app.baking.extensions.offline
import com.tomwo.app.baking.ui.adapters.StepAdapter
import com.tomwo.app.baking.ui.player.PlayerManager
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.tomwo.app.baking.R
import com.tomwo.app.baking.extensions.DelegatesExt
import kotlinx.android.synthetic.main.frag_detail.*
import org.jetbrains.anko.displayMetrics
import java.io.InvalidObjectException
import kotlin.math.roundToInt
import kotlin.properties.Delegates


class DetailFrag : Fragment(), Runnable, MediaUiManager
{
    /**
     * Companion Object
     */
    companion object {
        val TAG: String = DetailFrag::class.java.simpleName

        // factory method
        fun newInstance(item: Recipe): DetailFrag
        {
            val frag = DetailFrag()
            val args = Bundle()
            args.putParcelable(Recipe.ARG_RECIPE_ITEM, item)
            frag.arguments = args
            return frag
        }
    }

    /**
     * Instance Variables
     */
    private val handler: Handler = Handler()
    private val intentFilter: IntentFilter = IntentFilter()

    private var recipe: Recipe by Delegates.notNull()
    private var data: MutableList<Direction> by Delegates.notNull()
    private var videos: List<Direction>? = null
    private var isTablet: Boolean by DelegatesExt.notNullSingleValue()

    override var pauseToPlay: AnimatedVectorDrawable by Delegates.notNull()
    override var playToPause: AnimatedVectorDrawable by Delegates.notNull()
    override var playerManager: PlayerManager by Delegates.notNull()
    override lateinit var playPause: ImageView
    override lateinit var next: ImageView
    override lateinit var prev: ImageView

    private var detail: String = ""
    private var adapter: StepAdapter? = null

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            offline.visibility = if (context.offline()) View.VISIBLE else {
                playerManager.reloadSource(); View.GONE;
            }
        }
    }

    init {
        Log.d(TAG,"instantiated")
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
    }

    /**
     * onCreate()
     */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.retainInstance = true

        this.recipe = this.arguments!!.getParcelable(Recipe.ARG_RECIPE_ITEM) ?: throw InvalidObjectException("recipe is NULL!")
        Log.d(TAG, this.recipe.toString())

        val ctx = ctx!!

        this.isTablet = ctx.resources.getBoolean(R.bool.isTablet)
        this.playerManager = PlayerManager(ctx, this.recipe.name, this.recipe.steps!!, ::trackChangeHandler)

        this.pauseToPlay = ctx.drawable(R.drawable.avd_pause_to_play) as AnimatedVectorDrawable
        this.playToPause = ctx.drawable(R.drawable.avd_play_to_pause) as AnimatedVectorDrawable
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.frag_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        // expandableCard
        this.recipeDetails.setTitle(this.recipe.name)
        this.recipeDetails.setCaption(resources.getQuantityString(R.plurals.numberOfServings, recipe.servings, recipe.servings))
        this.recipeDetails.setDescription(resources.getString(R.string.ingredients_title))
        this.recipeDetails.setListData(this.recipe.ingredient!!)

        // layoutManager
        this.stepsList.layoutManager = LinearLayoutManager(ctx)
        this.stepsList.setHasFixedSize(true)
        this.stepsList.isNestedScrollingEnabled = false  // makes the scrolling uber-smooth
        this.playPause = this.exo_play_pause
        this.next = this.exo_next
        this.prev = this.exo_prev

        // protect against changing the orientation while video is currently playing
        // we get away with this b/c we're retaining the instance state
        if (playerManager.playWhenReady)
        {
            playPause.setImageDrawable(playToPause)
            playToPause.start()
            handler.postDelayed({ playerView.hideController(); nestedScrollView.scrollTo(0, 0) }, 100)
        }

        this.initMediaUi()

        if (this.detail != "") this.desc.text = this.detail

        doAsync {

            if (videos == null)
            {
                val list = recipe.steps!!

                // remove the introductions & set its videoURL to the videoURL of the next item in the list
                // we are assuming the 2nd item in the original list has no video, which is true for our dataSet
                data = list.filter { it.order > 1 }.toMutableList()

                data.add(0,  list[1].copy(videoURL = list[0].videoURL, thumbnailURL = list[0].thumbnailURL) )

                // get a list of all the videos in OUR list
                videos = data.filter { it.isVideo }
            }

            uiThread {

                // we need to cache this b/c the UI is defined in different layout files
                // todo: update to use ViewModel
                if (adapter == null) adapter = StepAdapter(data, { listItemClickListener(it) })
                stepsList.adapter = adapter

            }
        }
    }

    /**
     * @see MediaUiManager.onMediaClick
     */
    override fun onMediaClick()
    {
        handler.postDelayed(this,600)
    }


    /**
     * @see Runnable.run
     */
    override fun run()
    {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && playerManager.playWhenReady)
        {
            this.playerView.hideController()
        }
    }

    private fun listItemClickListener(item: Direction)
    {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return
        if (this.detail == item.detail) return  // protect against programmatically setting the selectedIndex
        this.detail    = item.detail
        this.desc.text = this.detail

        if (item.isVideo)
        {
            val index = videos?.indexOfFirst { it.id == item.id }
            index?.let {
                if (index == -1) return
                playerManager.seekTo(index)
            }
        }
    }

    private fun trackChangeHandler(videoIndex: Int?)
    {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) || videoIndex == null) return  // protect against bad state

        val temp: Direction = videos?.get(videoIndex) ?: return
        if (this.detail == temp.detail) return // don't update the selected list index if the user clicked the item!
        this.detail = temp.detail
        this.desc.text = this.detail

        if (stepsList.size == 0) return // happens in landscape

        // b/c this is videos, we need to find the next video in our list
        val index: Int = this.getListIndex(temp) ?: videoIndex

        val rect = Rect()
        this.stepsList[index].getGlobalVisibleRect(rect)

        val height: Int = ctx?.displayMetrics?.heightPixels ?: Int.MAX_VALUE

        if (this.isTablet)
        {
            // scroll to the item on the screen
            if (rect.top < 0)
            {
                // scroll to the position if the view is off-screen
                nestedScrollView.scrollTo(stepsList[index].x.roundToInt(), stepsList[index].y.roundToInt() - stepsList[index].height)
            } else if (rect.bottom >= height)
            {
                nestedScrollView.scrollTo(stepsList[index].x.roundToInt(), stepsList[index].y.roundToInt() + stepsList[index].height)
            }
        }

        if (index == 0)
        {
            nestedScrollView.scrollTo(0, 0)
        }

        handler.postDelayed({
            with(stepsList.findViewHolderForAdapterPosition(index)) {
                this?.itemView?.performClick()
            }
        }, 20)

    }

    private fun getListIndex(temp: Direction): Int?
    {
        var index: Int? = null
        data.forEachIndexed { i, item ->
            if (item.id == temp.id) {
                index = i
                return@forEachIndexed
            }
        }
        return index
    }


    override fun onStart()
    {
        super.onStart()
        if (Util.SDK_INT > 23) this.initializePlayer()

        // register connectivity BroadcastReceiver
        App.instance.registerReceiver(receiver, intentFilter)
    }

    override fun onResume()
    {
        super.onResume()
        if (Util.SDK_INT <= 23 || !playerManager.initialized) this.initializePlayer()
    }

    /**
     * Initialize the PlayerManager, which manages ExoPlayer
     */
    private fun initializePlayer() = playerManager.init(ctx!!, this.playerView)

    override fun onPause()
    {
        super.onPause()
        if (Util.SDK_INT <= 23) playerManager.reset()
    }

    override fun onStop()
    {
        super.onStop()
        if (Util.SDK_INT > 23) playerManager.reset()

        // register connectivity BroadcastReceiver
        App.instance.unregisterReceiver(receiver)
    }

    override fun onDestroyView()
    {
        playerManager.release()
        super.onDestroyView()
    }

}
