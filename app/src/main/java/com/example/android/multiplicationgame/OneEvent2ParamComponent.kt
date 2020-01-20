package com.example.android.multiplicationgame

abstract class OneEvent2ParamComponent<T1P1, T1P2>: OneEventComponent<(T1P1, T1P2) -> Unit>() {

    protected fun notifyListeners(param1: T1P1, param2: T1P2) {
        listeners.forEach { it(param1, param2) }
    }

}