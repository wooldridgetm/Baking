package com.tomwo.app.baking.data.server

import java.util.Arrays

/**
 * Created by wooldridgetm on 11/30/17.
 */

data class Recipe(val id: Int, inline val name: String, val ingredients: Array<Ingredient>, val steps: Array<Steps>, val servings: Int, val image: String = "")
{
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (name != other.name) return false
        if (servings != other.servings) return false
        if (image != other.image) return false

        // how to compare 2 arrays
        if (!(ingredients contentEquals other.ingredients)) return false
        if (!(steps contentEquals other.steps)) return false

        // if (!Arrays.equals(ingredients, other.ingredients)) return false
        // if (!Arrays.equals(steps, other.steps)) return false

        return true
    }

    override fun hashCode(): Int
    {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + Arrays.hashCode(ingredients)
        result = 31 * result + Arrays.hashCode(steps)
        result = 31 * result + servings
        result = 31 * result + image.hashCode()
        return result
    }
}


data class Steps(val id: Long, inline val shortDescription: String, inline val description: String, inline val videoURL: String?, inline val thumnailURL: String?)
data class Ingredient(val quantity: Double, val measure: String, val ingredient: String)

