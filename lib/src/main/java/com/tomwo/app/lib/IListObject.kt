package com.tomwo.app.lib

/**
 * interface for objects passed to my Adapter
 */
interface IListObject
{
    val label: String

    val accessoryLabel: String?
        get() = null
}