package com.raylib.java.game

import com.raylib.java.Raylib
import com.raylib.java.core.Color

fun main() {
    val raylib = Raylib()
    raylib.core.InitWindow(800, 600, "Game example 1")

    while (raylib.core.WindowShouldClose().not()) {
        raylib.core.BeginDrawing()
        raylib.core.ClearBackground(Color.WHITE)
        raylib.text.DrawText("Hello, World!\nThis is Hojat\nAnd he's working on his first raylib game", 500 - raylib.text.MeasureText("Hello, World!", 40) / 2, 300, 40, Color.DARKGRAY)
        raylib.core.EndDrawing()
    }
}