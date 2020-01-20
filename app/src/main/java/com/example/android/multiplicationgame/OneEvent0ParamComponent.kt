package com.example.android.multiplicationgame

abstract class OneEvent0ParamComponent: OneEventComponent<() -> Unit>() {

    protected fun notifyListeners() {
        listeners.forEach { it() }
    }

}