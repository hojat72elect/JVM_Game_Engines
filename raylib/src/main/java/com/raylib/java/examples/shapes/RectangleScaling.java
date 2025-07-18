package com.raylib.java.examples.shapes;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;

import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

public class RectangleScaling{

    /*******************************************************************************************
     *
     *   raylib-j [shapes] example - Rectangle Scaling by Mouse
     *
     *   This example has been created using raylib-j (Version 0.4)
     *   Ported by CreedVI
     *   https://github.com/creedvi/raylib-j
     *
     *   raylib is licensed under an unmodified zlib/libpng license
     *   Original example written and copyright by Ramon Santamaria (@raysan5)
     *   https://github.com/raysan5
     *
     *   Copyright (c) 2018 Vlad Adrian (@demizdor)
     *
     ********************************************************************************************/

    final static int MOUSE_SCALE_MARK_SIZE = 12;

    public static void main(String[] args){

        // Initialization
        //--------------------------------------------------------------------------------------
        final int screenWidth = 800;
        final int screenHeight = 450;
        Raylib rlj = new Raylib(screenWidth, screenHeight, "raylib [shapes] example - rectangle scaling mouse");
        Rectangle rec = new Rectangle(100, 100, 200, 80);
        Vector2 mousePosition;
        boolean mouseScaleReady, mouseScaleMode = false;

        rlj.core.SetTargetFPS(60);               // Set our game to run at 60 frames-per-second
        //--------------------------------------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose())    // Detect window close button or ESC key
        {
            // Update
            //----------------------------------------------------------------------------------
            mousePosition = rlj.core.GetMousePosition();

            if (rlj.shapes.CheckCollisionPointRec(mousePosition, rec) &&
                    rlj.shapes.CheckCollisionPointRec(mousePosition,
                            new Rectangle(rec.x + rec.width - MOUSE_SCALE_MARK_SIZE,
                            rec.y + rec.height - MOUSE_SCALE_MARK_SIZE, MOUSE_SCALE_MARK_SIZE, MOUSE_SCALE_MARK_SIZE)))
            {
                mouseScaleReady = true;
                if (rlj.core.IsMouseButtonPressed(MOUSE_BUTTON_LEFT.ordinal())) mouseScaleMode = true;
            }
            else mouseScaleReady = false;

            if (mouseScaleMode)
            {
                mouseScaleReady = true;

                rec.width = (mousePosition.x - rec.x);
                rec.height = (mousePosition.y - rec.y);

                if (rec.width < MOUSE_SCALE_MARK_SIZE) rec.width = MOUSE_SCALE_MARK_SIZE;
                if (rec.height < MOUSE_SCALE_MARK_SIZE) rec.height = MOUSE_SCALE_MARK_SIZE;

                if (rlj.core.IsMouseButtonReleased(MOUSE_BUTTON_LEFT.ordinal())) mouseScaleMode = false;
            }
            //----------------------------------------------------------------------------------

            // Draw
            //----------------------------------------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.RAYWHITE);

            rlj.text.DrawText("Scale rectangle dragging from bottom-right corner!", 10, 10, 20, Color.GRAY);

            rShapes.DrawRectangleRec(rec, rlj.textures.Fade(Color.GREEN, 0.5f));

            if (mouseScaleReady)
            {
                rlj.shapes.DrawRectangleLinesEx(rec, 1, Color.RED);
                rlj.shapes.DrawTriangle(new Vector2(rec.x + rec.width - MOUSE_SCALE_MARK_SIZE, rec.y + rec.height ),
                        new Vector2(rec.x + rec.width, rec.y + rec.height),
                        new Vector2(rec.x + rec.width, rec.y + rec.height - MOUSE_SCALE_MARK_SIZE ), Color.RED);
            }

            rlj.core.EndDrawing();
            //----------------------------------------------------------------------------------
        }
    }
}