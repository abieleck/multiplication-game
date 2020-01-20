package com.example.android.multiplicationgame

abstract class TwoEventsComponent<T1, T2>: OneEventComponent<T1>() {

    protected val event2Listeners: MutableList<T2> = ArrayList()

    protected fun addEvent2Listener(listener: T2) {
        event2Listeners.add(listener)
    }

    protected fun addEvent2Listeners(vararg listeners: T2) {
        event2Listeners.addAll(listeners)
    }

}