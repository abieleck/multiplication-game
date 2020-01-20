package com.example.android.multiplicationgame.model

class Question(a: Int, b: Int) {
    var a: Int = a
        set(value) {
            field = value
            answer = value * b
        }
    var b: Int = b
        set(value) {
            field = value
            answer = value * a
        }

    var answer = a * b
}