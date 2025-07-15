package com.raylib.java.core.rcamera;

import static com.raylib.java.core.input.Keyboard.KEY_A;
import static com.raylib.java.core.input.Keyboard.KEY_D;
import static com.raylib.java.core.input.Keyboard.KEY_E;
import static com.raylib.java.core.input.Keyboard.KEY_LEFT_ALT;
import static com.raylib.java.core.input.Keyboard.KEY_LEFT_CONTROL;
import static com.raylib.java.core.input.Keyboard.KEY_Q;
import static com.raylib.java.core.input.Keyboard.KEY_S;
import static com.raylib.java.core.input.Keyboard.KEY_W;
import static com.raylib.java.core.input.Mouse.MouseButton.MOUSE_BUTTON_MIDDLE;

import com.raylib.java.raymath.Vector2;

public class CameraData {

    public int
            MoveFront = KEY_W,
            MoveBack = KEY_S,
            MoveLeft = KEY_A,
            MoveRight = KEY_D,
            MoveUp = KEY_Q,
            MoveDown = KEY_E,
            smoothZoomControl = KEY_LEFT_CONTROL,
            altControl = KEY_LEFT_ALT,
            panControl = MOUSE_BUTTON_MIDDLE.ordinal();

    int mode;              // Current camera mode
    float targetDistance;           // rCamera distance from position to target
    float playerEyesPosition;       // Player eyes position from ground (in meters)
    Vector2 angle;                  // rCamera angle in plane XZ

    // rCamera movement control keys
    int[] moveControl;             // Move controls (CAMERA_FIRST_PERSON)

    public CameraData() {
        mode = Camera3D.CameraMode.CAMERA_CUSTOM;
        targetDistance = 0;
        playerEyesPosition = 1.85f;
        angle = new Vector2();
        moveControl = new int[]{
                MoveFront, MoveBack, MoveLeft, MoveRight, MoveUp, MoveDown
        };
    }
}
