package com.tomwo.app.lib

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Adapter for the RecyclerView
 *
 * the data must contain objects implementing IListObject
 * @see IListObject
 *
 *
 * TODO: allow for custom named properties
 * TODO: allow for positioning labels via code
 * TODO: allow for other types of display objects (icons)
 * TODO: allow for functions to be invoked to get these values at runtime
 */
class Adapter(private val data: List<IListObject>): RecyclerView.Adapter<Adapter.VH>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    class VH(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val tv: TextView  = itemView.findViewById(R.id.primaryLabel)
        private val tv2: TextView = itemView.findViewById(R.id.accessoryLabel)

        fun bind(item: IListObject) = with(item) {
            tv.text = label
            if (accessoryLabel != null && accessoryLabel != "") tv2.text = accessoryLabel
        }
    }

}

