package com.tomwo.app.baking.domain.commands

interface Command<out T>
{
    fun execute(): T
}