package com.tomwo.app.baking.data.server

import com.tomwo.app.baking.domain.Direction
import com.tomwo.app.baking.domain.Ingredient
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.utils.RegExr.Companion.PARENTHESIS
import com.tomwo.app.baking.utils.RegExr.Companion.PATTERN
import java.text.Normalizer
import com.tomwo.app.baking.data.server.Ingredient as ServerIngredient
import com.tomwo.app.baking.data.server.Recipe as ServerRecipe

class ServerDataMapper
{
    companion object
    {
        val TAG = ServerDataMapper::class.java.simpleName
    }

    fun convertToDomain(recipe: Array<ServerRecipe>): RecipeList = convertRecipeArrayToDomainList(recipe)

    private fun convertRecipeArrayToDomainList(recipeArr: Array<ServerRecipe>): RecipeList
    {
        val list = recipeArr.mapIndexed { _, recipe -> this.convertRecipeItemToDomain(recipe) }
        return RecipeList(list)
    }

    private fun convertRecipeItemToDomain(recipe: ServerRecipe): Recipe
    {
        val id: Long      = recipe.id.toLong()
        val name: String  = recipe.name
        val servings: Int = recipe.servings
        val image: String = recipe.image

        val ingredientList: List<Ingredient> = recipe.ingredients.mapIndexed { _, item -> Ingredient(item.quantity, item.measure, item.ingredient) }
        val directionsList: List<Direction> = recipe.steps.mapIndexed { _, step -> this.convertStepItemToDirection(step) }

        return Recipe(id, name, servings, image, directionsList, ingredientList)
    }

    private fun convertStepItemToDirection(step: Steps): Direction
    {
        val videoURL: String = step.videoURL?.trim() ?: ""
        val thumbnailURL: String = step.thumnailURL?.trim() ?: ""
        var detail: String = step.description
        var order: Int = -1

        // constants
        val period = "."
        val degree = "°"

        if (step.description.contains(period))
        {
            detail = step.description.substringAfter(period).trim()
            order  = step.description.substringBefore(period).trim().toInt()
        }

        var title = Normalizer.normalize(step.shortDescription, Normalizer.Form.NFKC)
        if (title != null) title = title.trim()
        if (title.indexOf(period) == -1)
        {
            title += period
        }

        detail = Normalizer.normalize(detail, Normalizer.Form.NFKC)

        if (detail.contains("�"))
        {
            detail = detail.replace("�", degree)
        }

        // now, clean up the data to make it more readable
        // todo: move to extension function
        title = title.replace(PATTERN," ").replace(PARENTHESIS, " (")
        val arr = title.split(" ").map {
            var s = it.trim().toLowerCase()

            // don't capitalize words 'or','and','the','other', and words btw parentheses
            if (s != "or" && s.indexOf("(") == -1 && s.indexOf(")") == -1 && s != "and" && s != "the" &&
                    s != "other" && s != "a" && s != "to" && s != "into" && s != "in" && s != "at" && s != "from" && s != "on" && s!="off")
            {
                s = s.capitalize()
            }
            if (s.indexOf(degree+"f") != -1)
            {
                s = s.toUpperCase()
            }
            s
        }
        title = arr.joinToString(" ")

        return Direction(step.id, order, title, detail, detail, videoURL, thumbnailURL)
    }
}