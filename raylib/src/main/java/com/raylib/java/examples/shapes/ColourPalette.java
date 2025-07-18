package com.raylib.java.examples.shapes;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;

import static com.raylib.java.core.Color.*;
import static com.raylib.java.core.input.Keyboard.KEY_SPACE;

public class ColourPalette{

    /*******************************************************************************************
     *
     *   raylib-j [shapes] example - Colour Palette
     *
     *   This example has been created using raylib-j (Version 0.4)
     *   Ported by CreedVI
     *   https://github.com/creedvi/raylib-j
     *
     *   raylib is licensed under an unmodified zlib/libpng license
     *   Original example written and copyright by Ramon Santamaria (@raysan5)
     *   https://github.com/raysan5
     *
     ********************************************************************************************/


    static final int MAX_COLORS_COUNT = 21;          // Number of colors available

    public static void main(String[] args) {
        // Initialization
        //--------------------------------------------------------------------------------------
        final int screenWidth = 800;
        final int screenHeight = 450;
        Raylib rlj = new Raylib();

        rlj.core.InitWindow(screenWidth, screenHeight, "raylib [shapes] example - colors palette");

        Color[] colors = {
            DARKGRAY, MAROON, ORANGE, DARKGREEN, DARKBLUE, DARKPURPLE, DARKBROWN,
            GRAY, RED, GOLD, LIME, BLUE, VIOLET, BROWN, LIGHTGRAY, PINK, YELLOW,
            GREEN, SKYBLUE, PURPLE, BEIGE
        };

        String[] colorNames = {
            "DARKGRAY", "MAROON", "ORANGE", "DARKGREEN", "DARKBLUE", "DARKPURPLE",
            "DARKBROWN", "GRAY", "RED", "GOLD", "LIME", "BLUE", "VIOLET", "BROWN",
            "LIGHTGRAY", "PINK", "YELLOW", "GREEN", "SKYBLUE", "PURPLE", "BEIGE"
        };

        Rectangle[] colorsRecs = new Rectangle[MAX_COLORS_COUNT];     // Rectangles array

        // Fills colorsRecs data (for every rectangle)
        for (int i = 0; i < MAX_COLORS_COUNT; i++)
        {
            colorsRecs[i] = new Rectangle();
            colorsRecs[i].x = 20 + 100*(i%7) + 10*(i%7);
            colorsRecs[i].y = 80 + 100*(i/7.0f) + 10*(i/7.0f);
            colorsRecs[i].width = 100;
            colorsRecs[i].height = 100;
        }

        boolean[] colorState = new boolean[MAX_COLORS_COUNT];           // Color state: 0-DEFAULT, 1-MOUSE_HOVER

        Vector2 mousePoint;

        rlj.core.SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
        //--------------------------------------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose())    // Detect window close button or ESC key
        {
            // Update
            //----------------------------------------------------------------------------------
            mousePoint = rlj.core.GetMousePosition();

            for (int i = 0; i < MAX_COLORS_COUNT; i++)
            {
                colorState[i] = rlj.shapes.CheckCollisionPointRec(mousePoint, colorsRecs[i]);
            }
            //----------------------------------------------------------------------------------

            // Draw
            //----------------------------------------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(RAYWHITE);

            rlj.text.DrawText("raylib colors palette", 28, 42, 20, BLACK);
            rlj.text.DrawText("press SPACE to see all colors", rlj.core.GetScreenWidth() - 180,
                    rlj.core.GetScreenHeight() - 40, 10, GRAY);

            for (int i = 0; i < MAX_COLORS_COUNT; i++)    // Draw all rectangles
            {
                rShapes.DrawRectangleRec(colorsRecs[i], rlj.textures.Fade(colors[i], colorState[i]? 0.6f : 1.0f));

                if (rlj.core.IsKeyDown(KEY_SPACE) || colorState[i])
                {
                    rlj.shapes.DrawRectangle((int)colorsRecs[i].x, (int)(colorsRecs[i].y + colorsRecs[i].height - 26),
                            (int)colorsRecs[i].width, 20, BLACK);
                    rlj.shapes.DrawRectangleLinesEx(colorsRecs[i], 6, rlj.textures.Fade(BLACK, 0.3f));
                    rlj.text.DrawText(colorNames[i],
                            (int)(colorsRecs[i].x + colorsRecs[i].width - rlj.text.MeasureText(colorNames[i], 10) - 12),
                            (int)(colorsRecs[i].y + colorsRecs[i].height - 20), 10, colors[i]);
                }
            }

            rlj.core.EndDrawing();
            //----------------------------------------------------------------------------------
        }
    }

}
