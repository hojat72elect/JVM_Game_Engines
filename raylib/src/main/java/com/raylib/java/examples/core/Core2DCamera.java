package com.raylib.java.examples.core;

import static com.raylib.java.core.input.Keyboard.KEY_A;
import static com.raylib.java.core.input.Keyboard.KEY_LEFT;
import static com.raylib.java.core.input.Keyboard.KEY_R;
import static com.raylib.java.core.input.Keyboard.KEY_RIGHT;
import static com.raylib.java.core.input.Keyboard.KEY_S;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.core.rcamera.Camera2D;
import com.raylib.java.raymath.Vector2;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;

public class Core2DCamera {

    /*******************************************************************************************
     *
     *   raylib-j [core] example - 2d camera
     *
     *   This example has been created using raylib-j (version 0.4)
     *   Ported by CreedVI
     *   https://github.com/creedvi/raylib-j
     *
     *   raylib is licensed under an unmodified zlib/libpng license
     *   Original example contributed by Ramon Santamaria (@raysan5)
     *   https://github.com/raysan5
     *
     *   Copyright (c) 2016 Ramon Santamaria (@raysan5)
     *
     ********************************************************************************************/

    final static int MAX_BUILDINGS = 100;

    public static void main(String[] args) {

        // Initialization
        //--------------------------------------------------------------------------------------
        int screenWidth = 800;
        int screenHeight = 450;

        Raylib rlj = new Raylib(screenWidth, screenHeight, "raylib-j [core] example - 2d camera");

        Rectangle player = new Rectangle(400, 280, 40, 40);
        Rectangle[] buildings = new Rectangle[MAX_BUILDINGS];
        Color[] buildColors = new Color[MAX_BUILDINGS];

        int spacing = 0;

        for (int i = 0; i < MAX_BUILDINGS; i++) {
            buildings[i] = new Rectangle();
            buildings[i].width = (float) rlj.core.GetRandomValue(50, 200);
            buildings[i].height = (float) rlj.core.GetRandomValue(100, 800);
            buildings[i].y = screenHeight - 130.0f - buildings[i].height;
            buildings[i].x = -6000.0f + spacing;

            spacing += (int) buildings[i].width;

            buildColors[i] = new Color(rlj.core.GetRandomValue(200, 240), rlj.core.GetRandomValue(200, 240),
                    rlj.core.GetRandomValue(200, 250), 255);
        }

        Camera2D camera = new Camera2D();
        camera.target = new Vector2(player.x + 20.0f, player.y + 20.0f);
        camera.offset = new Vector2(screenWidth / 2.0f, screenHeight / 2.0f);
        camera.rotation = 0.0f;
        camera.zoom = 1.0f;

        rlj.core.SetTargetFPS(60);                   // Set our game to run at 60 frames-per-second
        //--------------------------------------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose())        // Detect window close button or ESC key
        {
            // Update
            //----------------------------------------------------------------------------------

            // Player movement
            if (rlj.core.IsKeyDown(KEY_RIGHT)) {
                player.x += 2;
            } else if (rlj.core.IsKeyDown(KEY_LEFT)) {
                player.x -= 2;
            }

            // Camera target follows player
            camera.target = new Vector2(player.x + 20, player.y + 20);

            // Camera rotation controls
            if (rlj.core.IsKeyDown(KEY_A)) {
                camera.rotation--;
            } else if (rlj.core.IsKeyDown(KEY_S)) {
                camera.rotation++;
            }

            // Limit camera rotation to 80 degrees (-40 to 40)
            if (camera.rotation > 40) {
                camera.rotation = 40;
            } else if (camera.rotation < -40) {
                camera.rotation = -40;
            }

            // Camera zoom controls
            camera.zoom += rlj.core.GetMouseWheelMove() * 0.05f;

            if (camera.zoom > 3.0f) {
                camera.zoom = 3.0f;
            } else if (camera.zoom < 0.1f) {
                camera.zoom = 0.1f;
            }

            // Camera reset (zoom and rotation)
            if (rlj.core.IsKeyPressed(KEY_R)) {
                camera.zoom = 1.0f;
                camera.rotation = 0.0f;
            }
            //----------------------------------------------------------------------------------

            // Draw
            //----------------------------------------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.RAYWHITE);

            rlj.core.BeginMode2D(camera);

            rlj.shapes.DrawRectangle(-6000, 320, 13000, 8000, Color.DARKGRAY);

            for (int i = 0; i < MAX_BUILDINGS; i++) {
                rShapes.DrawRectangleRec(buildings[i], buildColors[i]);
            }

            rShapes.DrawRectangleRec(player, Color.RED);

            rlj.shapes.DrawLine((int) camera.target.x, -screenHeight * 10, (int) camera.target.x, screenHeight * 10, Color.GREEN);
            rlj.shapes.DrawLine(-screenWidth * 10, (int) camera.target.y, screenWidth * 10, (int) camera.target.y, Color.GREEN);

            rlj.core.EndMode2D();

            rlj.text.DrawText("SCREEN AREA", 640, 10, 20, Color.RED);

            rlj.shapes.DrawRectangle(0, 0, screenWidth, 5, Color.RED);
            rlj.shapes.DrawRectangle(0, 5, 5, screenHeight - 10, Color.RED);
            rlj.shapes.DrawRectangle(screenWidth - 5, 5, 5, screenHeight - 10, Color.RED);
            rlj.shapes.DrawRectangle(0, screenHeight - 5, screenWidth, 5, Color.RED);

            rlj.shapes.DrawRectangle(10, 10, 250, 113, rlj.textures.Fade(Color.SKYBLUE, 0.5f));
            rlj.shapes.DrawRectangleLines(10, 10, 250, 113, Color.BLUE);

            rlj.text.DrawText("Free 2d camera controls:", 20, 20, 10, Color.BLACK);
            rlj.text.DrawText("- Right/Left to move Offset", 40, 40, 10, Color.DARKGRAY);
            rlj.text.DrawText("- Mouse Wheel to Zoom in-out", 40, 60, 10, Color.DARKGRAY);
            rlj.text.DrawText("- A / S to Rotate", 40, 80, 10, Color.DARKGRAY);
            rlj.text.DrawText("- R to reset Zoom and Rotation", 40, 100, 10, Color.DARKGRAY);

            rlj.core.EndDrawing();
            //----------------------------------------------------------------------------------
        }

        // De-Initialization
        //--------------------------------------------------------------------------------------
        //--------------------------------------------------------------------------------------
    }
}
