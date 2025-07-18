package com.raylib.java.examples.textures;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;
import com.raylib.java.rlgl.RLGL;
import com.raylib.java.shapes.Rectangle;
import com.raylib.java.shapes.rShapes;
import com.raylib.java.textures.Image;
import com.raylib.java.textures.Texture2D;

import static com.raylib.java.core.input.Keyboard.KEY_DOWN;
import static com.raylib.java.core.input.Keyboard.KEY_UP;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static com.raylib.java.examples.textures.ImageProcessing.ImageProcess.COLOR_BRIGHTNESS;
import static com.raylib.java.examples.textures.ImageProcessing.ImageProcess.NONE;

public class ImageProcessing{

    /*******************************************************************************************
     *
     *   raylib-j [textures] example - Image processing
     *
     *   NOTE: Images are loaded in CPU memory (RAM); textures are loaded in GPU memory (VRAM)
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


    static final int NUM_PROCESSES = 8;

    static class ImageProcess{
        final static int NONE = 0,
                COLOR_GRAYSCALE = 1,
                COLOR_TINT = 2,
                COLOR_INVERT = 3,
                COLOR_CONTRAST = 4,
                COLOR_BRIGHTNESS = 5,
                FLIP_VERTICAL = 6,
                FLIP_HORIZONTAL = 7;
    }

    static String[] processText = {
            "NO PROCESSING",
            "COLOR GRAYSCALE",
            "COLOR TINT",
            "COLOR INVERT",
            "COLOR CONTRAST",
            "COLOR BRIGHTNESS",
            "FLIP VERTICAL",
            "FLIP HORIZONTAL"
    };

    public static void main(String[] args){

        // Initialization
        //--------------------------------------------------------------------------------------
        int screenWidth = 800;
        int screenHeight = 450;

        Raylib rlj = new Raylib(screenWidth, screenHeight, "raylib-j [textures] example - image processing");

        // NOTE: Textures MUST be loaded after Window initialization (OpenGL context is required)

        Image imOrigin = rlj.textures.LoadImage("resources/parrots.png");   // Loaded in CPU memory (RAM)
        rlj.textures.ImageFormat(imOrigin, RLGL.rlPixelFormat.RL_PIXELFORMAT_UNCOMPRESSED_R8G8B8A8); // Format
        // image to RGBA 32bit (required for texture update) <-- ISSUE
        Texture2D texture = rlj.textures.LoadTextureFromImage(imOrigin);    // Image converted to texture, GPU memory
        // (VRAM)

        Image imCopy = rlj.textures.ImageCopy(imOrigin);

        int currentProcess = 0;
        boolean textureReload = false;

        Rectangle[] toggleRecs = new Rectangle[NUM_PROCESSES];
        int mouseHoverRec = -1;

        for (int i = 0; i < NUM_PROCESSES; i++){
            toggleRecs[i] = new Rectangle(40.0f, (float) (50 + 32 * i), 150.0f, 30.0f);
        }

        rlj.core.SetTargetFPS(60);
        //---------------------------------------------------------------------------------------

        // Main game loop
        while (!rlj.core.WindowShouldClose()){    // Detect window close button or ESC key
            // Update
            //----------------------------------------------------------------------------------

            // Mouse toggle group logic
            for (int i = 0; i < NUM_PROCESSES; i++){
                if (rlj.shapes.CheckCollisionPointRec(rlj.core.GetMousePosition(), toggleRecs[i])){
                    mouseHoverRec = i;

                    if (rlj.core.IsMouseButtonReleased(MOUSE_BUTTON_LEFT.ordinal())){
                        currentProcess = i;
                        textureReload = true;
                    }
                    break;
                }
                else{
                    mouseHoverRec = -1;
                }
            }

            // Keyboard toggle group logic
            if (rlj.core.IsKeyPressed(KEY_DOWN)){
                currentProcess++;
                if (currentProcess > (NUM_PROCESSES - 1)) currentProcess = NONE;
                textureReload = true;
            }
            else if (rlj.core.IsKeyPressed(KEY_UP)){
                currentProcess--;
                if (currentProcess < NONE) currentProcess = ImageProcess.FLIP_HORIZONTAL;
                textureReload = true;
            }

            // Reload texture when required
            if (textureReload){
                imCopy = rlj.textures.ImageCopy(imOrigin);     // Restore image-copy from image-origin

                // NOTE: Image processing is a costly CPU process to be done every frame,
                // If image processing is required in a frame-basis, it should be done
                // with a texture and by shaders
                switch (currentProcess) {
                    case ImageProcess.COLOR_GRAYSCALE -> rlj.textures.ImageColorGrayscale(imCopy);
                    case ImageProcess.COLOR_TINT -> rlj.textures.ImageColorTint(imCopy, Color.GREEN);
                    case ImageProcess.COLOR_INVERT -> rlj.textures.ImageColorInvert(imCopy);
                    case ImageProcess.COLOR_CONTRAST -> rlj.textures.ImageColorContrast(imCopy, -40);
                    case COLOR_BRIGHTNESS -> rlj.textures.ImageColorBrightness(imCopy, -80);
                    case ImageProcess.FLIP_VERTICAL -> rlj.textures.ImageFlipVertical(imCopy);
                    case ImageProcess.FLIP_HORIZONTAL -> rlj.textures.ImageFlipHorizontal(imCopy);
                    default -> {
                    }
                }

                Color[] pixels = rlj.textures.LoadImageColors(imCopy);    // Load pixel data from image (RGBA 32bit)
                rlj.textures.UpdateTexture(texture, pixels);             // Update texture with new image data
                rlj.textures.UnloadImageColors(pixels);                  // Unload pixels data from RAM

                textureReload = false;
            }
            //----------------------------------------------------------------------------------

            // Draw
            //----------------------------------------------------------------------------------
            rlj.core.BeginDrawing();

            rlj.core.ClearBackground(Color.RAYWHITE);

            rlj.text.DrawText("IMAGE PROCESSING:", 40, 30, 10, Color.DARKGRAY);

            // Draw rectangles
            for (int i = 0; i < NUM_PROCESSES; i++){
                rShapes.DrawRectangleRec(toggleRecs[i], ((i == currentProcess) || (i == mouseHoverRec)) ?
                        Color.SKYBLUE : Color.LIGHTGRAY);
                rlj.shapes.DrawRectangleLines((int) toggleRecs[i].x, (int) toggleRecs[i].y, (int) toggleRecs[i].width,
                                              (int) toggleRecs[i].height, ((i == currentProcess) || (i == mouseHoverRec)) ? Color.BLUE :
                                                      Color.GRAY);
                rlj.text.DrawText(processText[i],
                                  (int) (toggleRecs[i].x + toggleRecs[i].width / 2 - rlj.text.MeasureText(processText[i], 10) / 2),
                                  (int) toggleRecs[i].y + 11, 10, ((i == currentProcess) || (i == mouseHoverRec)) ?
                                          Color.DARKBLUE : Color.DARKGRAY);
            }

            rlj.textures.DrawTexture(texture, screenWidth - texture.width - 60, screenHeight / 2 - texture.height / 2,
                                     Color.WHITE);
            rlj.shapes.DrawRectangleLines(screenWidth - texture.width - 60, screenHeight / 2 - texture.height / 2,
                                          texture.width, texture.height, Color.BLACK);

            rlj.core.EndDrawing();
            //----------------------------------------------------------------------------------
        }

        // De-Initialization
        //--------------------------------------------------------------------------------------
        rlj.textures.UnloadTexture(texture);       // Unload texture from VRAM
        rlj.textures.UnloadImage(imOrigin);        // Unload image-origin from RAM
        rlj.textures.UnloadImage(imCopy);          // Unload image-copy from RAM
        //--------------------------------------------------------------------------------------
    }

}
