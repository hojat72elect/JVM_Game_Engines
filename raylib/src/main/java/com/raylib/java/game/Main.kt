package com.raylib.java.game

import com.raylib.java.Raylib
import com.raylib.java.core.Color

fun main() {
    val raylib = Raylib()
    raylib.core.InitWindow(800, 600, "Raylib Java Example")

    while (raylib.core.WindowShouldClose().not()) {
        raylib.core.BeginDrawing()
        raylib.core.ClearBackground(Color.WHITE)
        raylib.text.DrawText("Hello World!", 500 - (raylib.text.MeasureText("Hello World!", 20) / 2), 300, 20, Color.DARKGRAY)
        raylib.core.EndDrawing()
    }
}