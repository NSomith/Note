package com.example.notes.other

open class Event<out T>(private val content:T) {

    var hasbeenHandles = false
    private set

    fun getContentIfNotHandled() = if(hasbeenHandles){
        null
    }else{
        hasbeenHandles = true
        content
    }

    fun peekContent() = content
}