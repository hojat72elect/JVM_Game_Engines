package com.raylib.java.game

import com.raylib.java.Raylib
import com.raylib.java.core.Color

fun main() {
    val rlj = Raylib()
    rlj.core.InitWindow(800, 600, "Raylib-J Example")

    while (!rlj.core.WindowShouldClose()) {
        rlj.core.BeginDrawing()
        rlj.core.ClearBackground(Color.WHITE)
        rlj.text.DrawText("Hello, World!", 500 - rlj.text.MeasureText("Hello, World!", 40) / 2, 300, 40, Color.DARKGRAY)
        rlj.core.EndDrawing()
    }
}