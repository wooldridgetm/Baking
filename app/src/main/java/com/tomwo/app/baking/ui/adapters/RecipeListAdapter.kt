package com.tomwo.app.baking.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tomwo.app.baking.R
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.extensions.ctx

class RecipeListAdapter(private val recipeList: RecipeList,
                        private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeListAdapter.RecipeViewHolder =
            RecipeViewHolder(LayoutInflater.from(parent.ctx).inflate(R.layout.list_item_recipe, parent, false), itemClick)

    override fun onBindViewHolder(holder: RecipeListAdapter.RecipeViewHolder, position: Int) = holder.bindRecipe(recipeList[position])
    override fun getItemCount() = recipeList.size

    class RecipeViewHolder(view: View, private val itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(view)
    {
        // these aren't cached in Adapter-based widgets!
        private val title: TextView  = itemView.findViewById(R.id.title)
        private val detail: TextView = itemView.findViewById(R.id.details)
        private val numOfSteps: TextView = itemView.findViewById(R.id.numOfSteps)

        fun bindRecipe(recipe: Recipe) = with(recipe) {
            title.text = name
            detail.text = itemView.ctx.resources.getQuantityString(R.plurals.numberOfServings, servings, servings)
            numOfSteps.text = "(${steps!!.size - 1} steps)"
            itemView.setOnClickListener { itemClick(adapterPosition) }
        }
    }
}