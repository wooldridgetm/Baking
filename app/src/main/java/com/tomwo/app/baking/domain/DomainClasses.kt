package com.tomwo.app.baking.domain

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.tomwo.app.baking.extensions.DelegatesExt
import com.tomwo.app.lib.IListObject
import kotlin.properties.Delegates

/**
 * Created by wooldridgetm on 11/29/17.
 */

data class RecipeList(val recipeList : List<Recipe>)
{
    val size: Int
        get() = recipeList.size

    operator fun get(index: Int) = recipeList[index]
}

data class Servings(val servings: Int)


data class Recipe(val id: Long, inline val name: String, val servings: Int, val image: String, inline val steps: List<Direction>?, inline val ingredient: List<Ingredient>?) : Parcelable
{
    constructor(source: Parcel) : this(
            source.readLong(),
            source.readString(),
            source.readInt(),
            source.readString(),
            source.createTypedArrayList(Direction.CREATOR),
            source.createTypedArrayList(Ingredient.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeLong(id)
        writeString(name)
        writeInt(servings)
        writeString(image)
        writeTypedList(steps)
        writeTypedList(ingredient)
    }

    companion object
    {
        const val ARG_RECIPE_ITEM = "arg-current-recipe-item"

        @JvmField
        val CREATOR: Parcelable.Creator<Recipe> = object : Parcelable.Creator<Recipe>
        {
            override fun createFromParcel(source: Parcel): Recipe = Recipe(source)
            override fun newArray(size: Int): Array<Recipe?> = arrayOfNulls(size)
        }
    }
}

/**
 * @param [id] (Int)
 * @param [title] (String)
 * @param [order] (Int)
 * @param [detail] (String)
 * @param [isVideo] (Boolean)
 * @param [uri] (Uri)
 */
data class Direction(val id: Long, inline val order: Int, inline val title: String, inline val detail: String, inline val detail2: String, val videoURL: String, val thumbnailURL: String) : Parcelable
{
    val isVideo: Boolean by lazy { (this.videoURL != "" || this.thumbnailURL != "") }

    val uri: Uri?        by lazy { (if (videoURL != "") Uri.parse(videoURL) else null) }

    var refID: Long       by DelegatesExt.notNullSingleValue()

    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())
    {
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeLong(id)
        parcel.writeInt(order)
        parcel.writeString(title)
        parcel.writeString(detail)
        parcel.writeString(detail2)
        parcel.writeString(videoURL)
        parcel.writeString(thumbnailURL)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Direction>
    {
        override fun createFromParcel(parcel: Parcel): Direction = Direction(parcel)
        override fun newArray(size: Int): Array<Direction?> = arrayOfNulls(size)
    }


}


data class Ingredient(val quantity: Double, val measure: String, val ingredient: String) : Parcelable,
                                                                                           IListObject
{
    var id: Long   by DelegatesExt.notNullSingleValue()
    var refID: Int by DelegatesExt.notNullSingleValue()

    override val label: String
        get() = ingredient

    override val accessoryLabel: String?
        get() = "$quantity $measure"

    constructor(source: Parcel) : this(
            source.readDouble(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(quantity)
        writeString(measure)
        writeString(ingredient)
    }

    companion object
    {
        @JvmField
        val CREATOR: Parcelable.Creator<Ingredient> = object : Parcelable.Creator<Ingredient>
        {
            override fun createFromParcel(source: Parcel): Ingredient = Ingredient(source)
            override fun newArray(size: Int): Array<Ingredient?> = arrayOfNulls(size)
        }
    }
}

