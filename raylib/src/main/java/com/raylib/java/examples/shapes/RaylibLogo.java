package com.raylib.java.examples.shapes;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class RaylibLogo{

    /*******************************************************************************************
     *
     *   raylib [shapes] example - Draw raylib logo using basic shapes
     *
     *   This example has been created using raylib-j (Version 0.4)
     *   Ported by CreedVI
     *   https://github.com/creedvi/raylib-j
     *
     *  raylib is licensed under an unmodified zlib/libpng license
     *  Original example written and copyright by Ramon Santamaria (@raysan5)
     *  https://github.com/raysan5
     ********************************************************************************************/

    public static void main(String[] args){

        // Initialization
        //--------------------------------------------------------------------------------------
        final int screenWidth = 800;
        final int screenHeight = 450;
        Raylib rlj = new Raylib(screenWidth, screenHeight, "raylib [shapes] example - raylib logo using shapes");

        rlj.core.SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
        //--------------------------------------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose())    // Detect window close button or ESC key
        {
            // Update
            //----------------------------------------------------------------------------------
            // TODO.txt: Update your variables here
            //----------------------------------------------------------------------------------

            // Draw
            //----------------------------------------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.RAYWHITE);

            rlj.shapes.DrawRectangle(screenWidth/2 - 128, screenHeight/2 - 128, 256, 256, Color.BLACK);
            rlj.shapes.DrawRectangle(screenWidth/2 - 112, screenHeight/2 - 112, 224, 224, Color.RAYWHITE);
            rlj.text.DrawText("raylib", screenWidth/2 - 44, screenHeight/2 + 48, 50, Color.BLACK);

            rlj.text.DrawText("this is NOT a texture!", 350, 370, 10, Color.GRAY);

            rlj.core.EndDrawing();
            //----------------------------------------------------------------------------------
        }
    }
}