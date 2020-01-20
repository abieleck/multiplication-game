package com.example.android.multiplicationgame

abstract class ThreeEvents0P0P0PComponent: ThreeEventsComponent<() -> Unit, () -> Unit, () -> Unit>() {

    protected fun notifyEvent1Listeners() {
        listeners.forEach { it() }
    }

    protected fun notifyEvent2Listeners() {
        event2Listeners.forEach { it() }
    }

    protected fun notifyEvent3Listeners() {
        event3listeners.forEach { it() }
    }

}