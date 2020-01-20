package com.example.android.multiplicationgame

abstract class OneEvent1ParamComponent<T1P1>: OneEventComponent<(T1P1) -> Unit>() {

    protected fun notifyListeners(param: T1P1) {
        listeners.forEach { it(param) }
    }

}