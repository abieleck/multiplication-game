package com.example.android.multiplicationgame

import com.example.android.multiplicationgame.view.GameComponent

abstract class OneEventComponent<T>: GameComponent() {

    protected val listeners: MutableList<T> = ArrayList()

    protected fun addEvent1Listener(listener: T) {
        listeners.add(listener)
    }

    protected fun addEvent1Listeners(vararg listeners: T) {
        this.listeners.addAll(listeners)
    }


}