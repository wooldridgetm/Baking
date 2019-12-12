package com.tomwo.app.lib

import android.content.Context
import android.support.v7.content.res.AppCompatResources
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.widget.TextView

class CollapsibleCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr)
{
    private var expanded = false

    // we must set these in code b/c Kotlin Extensions not be able to cache them like it does in a Fragment/Activity.
    private val titleContainer: View
    private val cardTitle: TextView
    private val cardCaption: TextView
    private val tweenIcon: ImageView
    private val cardDesc: TextView
    private val recyclerView: RecyclerView

    init
    {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleCard, 0, 0)
        val cardTitle = arr.getString(R.styleable.CollapsibleCard_cardTitle)
        val cardCaption = arr.getString(R.styleable.CollapsibleCard_cardCaption)
        val cardDescription = arr.getString(R.styleable.CollapsibleCard_cardDescription)
        // NOTE: I should also specify a way to set the list data in XML when using dataBinding, but I haven't gotten this to work yet

        arr.recycle()

        // attachToRoot = true!
        val rootView = LayoutInflater.from(context).inflate(R.layout.collapsible_card, this, true)

        this.cardTitle = rootView.findViewById(R.id.cardTitle)
        this.cardTitle.text = cardTitle
        this.setTitleContentDescription(cardTitle)  // private fx call

        this.cardCaption = rootView.findViewById(R.id.cardCaption)
        this.cardCaption.text = cardCaption

        this.tweenIcon = rootView.findViewById(R.id.tweenIcon)

        this.cardDesc = rootView.findViewById(R.id.cardDesc)
        this.cardDesc.text = cardDescription

        if (SDK_INT < M)
        {
            this.tweenIcon.imageTintList = AppCompatResources.getColorStateList(context, R.color.collapsing_section)
        }

        this.recyclerView = rootView.findViewById(R.id.cardList)
        this.recyclerView.layoutManager = LinearLayoutManager(context)

        val toggle = TransitionInflater.from(context).inflateTransition(R.transition.toggle_card)

        this.titleContainer = rootView.findViewById(R.id.titleContainer)
        this.titleContainer.setOnClickListener {
            expanded = !expanded
            toggle.duration = if (expanded) 300L else 200L
            TransitionManager.beginDelayedTransition(rootView.parent as ViewGroup, toggle)

            this.cardDesc.visibility = if (expanded && !TextUtils.isEmpty(cardDesc.text)) View.VISIBLE else View.GONE
            recyclerView.visibility = if (expanded && recyclerView.adapter != null) View.VISIBLE else View.GONE
            tweenIcon.rotation = if (expanded) 180f else 0f

            // activated used to tint controls when expanded
            tweenIcon.isActivated = expanded
            this.cardTitle.isActivated = expanded
            this.cardCaption.isActivated = expanded

            this.setTitleContentDescription(cardTitle)
        }
    }


    fun setTitle(value: String)
    {
        this.cardTitle.text = value
    }

    fun setCaption(value: String)
    {
        this.cardCaption.text = value
    }

    fun setDescription(description: String)
    {
        cardDesc.text = description
    }

    fun setListData(data: List<IListObject>)
    {
        this.recyclerView.adapter = Adapter(data)
    }

    private fun setTitleContentDescription(cardTitle: String?)
    {
        this.cardTitle.contentDescription = cardTitle + ", " + if (expanded) resources.getString(R.string.expanded) else resources.getString(R.string.collapsed)
    }
}
