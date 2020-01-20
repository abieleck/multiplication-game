package com.example.android.multiplicationgame

abstract class ThreeEventsComponent<T1, T2, T3>: TwoEventsComponent<T1, T2>() {

    protected val event3listeners: MutableList<T3> = ArrayList()

    protected fun addEvent3Listener(listener: T3) {
        event3listeners.add(listener)
    }

    protected fun addEvent3Listeners(vararg listeners: T3) {
        event3listeners.addAll(listeners)
    }

}