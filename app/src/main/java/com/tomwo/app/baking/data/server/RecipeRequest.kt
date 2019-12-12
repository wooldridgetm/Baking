package com.tomwo.app.baking.data.server

import com.google.gson.Gson
import java.net.URL
import java.net.UnknownHostException

class RecipeRequest(val gson: Gson = Gson())
{
    companion object {
        private const val RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json"
        private const val DEFAULT_RESPONSE: String = "[]"
    }

    fun execute(): Array<Recipe>
    {
        var recipeJsonStr: String = DEFAULT_RESPONSE
        try
        {
            recipeJsonStr = URL(RECIPE_URL).readText()
        }
        catch (e: UnknownHostException)
        {
            e.printStackTrace()
            // not connected to the internet
        }

        return gson.fromJson(recipeJsonStr, Array<Recipe>::class.java)
    }

}