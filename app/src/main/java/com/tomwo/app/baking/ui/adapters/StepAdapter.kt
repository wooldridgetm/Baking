package com.tomwo.app.baking.ui.adapters

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.tomwo.app.baking.R
import com.tomwo.app.baking.domain.Direction
import com.tomwo.app.baking.extensions.color
import com.tomwo.app.baking.extensions.ctx
import com.tomwo.app.baking.extensions.getString
import kotlin.coroutines.experimental.coroutineContext

class StepAdapter(private val data: List<Direction>,
                  private val click: (Direction) -> Unit,
                  private val allowMultiSelection: Boolean = false): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    companion object {

        private const val VIEW_TYPE_SPACER: Int = 666
        private const val VIEW_TYPE_DEFAULT: Int = 667

        private fun getColors(selected: Boolean) = if (selected) Pair(R.color.selected_list_item, android.R.color.black) else Pair(android.R.color.white, R.color.primary_text_dark)

        private fun toggleSelected(view: View, tv: TextView, value: Boolean) {
            val colors: Pair<Int, Int> = getColors(value)

            view.setBackgroundColor( view.ctx.color(colors.first) )
            tv.setTextColor( view.ctx.color(colors.second) )

            tv.typeface = if(value) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
    }



    private var selectedIndex: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
            VIEW_TYPE_SPACER ->
                VH2( LayoutInflater.from(parent.ctx).inflate(R.layout.spacer, parent, false) )
            else ->
                VH( LayoutInflater.from(parent.ctx).inflate(R.layout.list_item_direction, parent, false), click)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int): Unit = (holder as? VH)?.bindData(data[i]) ?: Unit

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int = if (data[position].order == -1) VIEW_TYPE_SPACER else VIEW_TYPE_DEFAULT

    /**
     * ViewHolder
     */
    inner class VH( itemView: View, private val onClick: (Direction) -> Unit) : RecyclerView.ViewHolder(itemView)
    {
        // anko doesn't cache these when outside Activity/Fragment
        private val orderV:TextView = itemView.findViewById(R.id.orderTextView)
        private val titleV:TextView = itemView.findViewById(R.id.titleTextView)
        private val detailV:TextView = itemView.findViewById(R.id.detailTextView)
        private val vid: ImageView = itemView.findViewById(R.id.videoIcon)

        private val isTablet: Boolean = itemView.ctx.resources.getBoolean(R.bool.isTablet)

        fun bindData( item: Direction) = with(item) {
            orderV.text = getString(R.string.ordering, order)
            titleV.text = title
            detailV.text = detail

            vid.visibility = if (isVideo) View.VISIBLE else View.INVISIBLE

            if (isVideo || isTablet)
            {
                itemView.isClickable  = true
                itemView.isFocusable = true

                itemView.setOnClickListener {

                    val index: Int = adapterPosition
                    if (index == RecyclerView.NO_POSITION) return@setOnClickListener

                    if (!allowMultiSelection && index != selectedIndex)
                    {
                        data[selectedIndex].isSelected = !data[selectedIndex].isSelected
                        notifyItemChanged(selectedIndex)
                    }

                    // store a new reference to selectedIndex
                    selectedIndex = index
                    isSelected = !isSelected

                    toggleSelected(itemView, titleV, isSelected)

                    // trigger the onClickHandler
                    onClick(this)
                }
            } else
            {
                itemView.isClickable  = false
                itemView.isFocusable = false
            }

            toggleSelected(itemView, titleV, isSelected)
        }
    }

    class VH2(view: View): RecyclerView.ViewHolder(view)
}