<img align="left" src="https://github.com/CreedVI/Raylib-J/blob/main/logo/raylib-j_256x256.png" width=256 alt="Raylib-J Logo">

# Raylib-J


[Raylib](https://github.com/raysan5/raylib) implemented in Java using [LWJGL3](https://www.lwjgl.org/).

**Raylib-J is currently up-to-date with the 4.2 release of Raylib**<br>
Raylib-J is meant to be a one-for-one rewrite of Raylib with some quality of life changes including, but not limited 
to: JavaDoc comments, `DrawFPS(int posX, int posY, Color theColorYouWant)`, and `CloseWindow()` being handled 
automatically!

---

## Basic Example

Here's all the code needed to create a window and render some text:

```java
package example;

import com.raylib.java.Raylib;
import com.raylib.java.core.Color;

public class example{

    public static void main(String[] args){
        Raylib rlj = new Raylib();
        rlj.core.InitWindow(800, 600, "Raylib-J Example");

        while (!rlj.core.WindowShouldClose()){
            rlj.core.BeginDrawing();
            rlj.core.ClearBackground(Color.WHITE);
            rlj.text.DrawText("Hello, World!", 800 - (rlj.text.MeasureText("Hello, World!", 20)/2), 300, 20, Color.DARKGRAY);
            rlj.core.EndDrawing();
        }
    }

}
```

More examples like the one above can be found at the [Examples repo](https://github.com/CreedVI/Raylib-J-Examples), or you can 
see the wiki for additional documentation and elaboration!
---

## Using Raylib-J

Raylib-J is split between the following modules:
 * Core: Contains all basic Raylib functions.
 * Models: Load and render models and render 3D Geometry
 * Audio: Load, manipulate, and play audio
 * RLGL: Raylib's OpenGL abstraction layer. 
 * Shapes: Need to draw 2D shapes and check collision between them? Look no further.
 * Text: Manipulate and render text using the default Raylib font, or import your own!
 * Textures: All your texture and image needs.
 * Utils: A Raylib-J specific module that contains things like `rLights`, `FileIO`, and `rEasings`

Check the [Raylib Cheatsheet](https://www.raylib.com/cheatsheet/cheatsheet.html) to see each module's available methods!

---

## Development Status:

Here's the quick list:

[X] rCore <br>
[X] rShapes <br>
[X] rTextures <br>
[X] rText <br>
[X] rModels <br>
[o] rAudio <br>
[X] RLGL <br>
[X] Raymath <br>
[X] Physac <br>
[X] easings <br>
[X] rLights <br>

<b>Key:</b>
X - complete |
O - nearing completion |
o - in progress |
p - postponed
