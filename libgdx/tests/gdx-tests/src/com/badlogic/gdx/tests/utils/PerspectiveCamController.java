package com.badlogic.gdx.tests.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class PerspectiveCamController extends InputAdapter {
    private final static Vector3 tmpV = new Vector3();

    public PerspectiveCamera cam;
    Vector3 lookAt = new Vector3();
    TransformMode mode = TransformMode.Translate;
    boolean translated = false;
    Vector2 tCurr = new Vector2();
    Vector2 last = new Vector2();
    Vector2 delta = new Vector2();
    Vector2 currWindow = new Vector2();
    Vector2 lastWindow = new Vector2();
    Vector3 curr3 = new Vector3();
    Vector3 delta3 = new Vector3();
    Plane lookAtPlane = new Plane(new Vector3(0, 1, 0), 0);
    Matrix4 rotMatrix = new Matrix4();
    Vector3 xAxis = new Vector3(1, 0, 0);
    Vector3 yAxis = new Vector3(0, 1, 0);
    Vector3 point = new Vector3();

    public PerspectiveCamController(PerspectiveCamera cam) {
        this.cam = cam;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        mode = TransformMode.Rotate;
        last.set(x, y);
        tCurr.set(x, y);
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        mode = TransformMode.None;
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (pointer != 0) return false;
        delta.set(x, y).sub(last);

        if (mode == TransformMode.Rotate) {
            point.set(cam.position).sub(lookAt);

            if (tmpV.set(point).nor().dot(yAxis) < 0.9999f) {
                xAxis.set(cam.direction).crs(yAxis).nor();
                rotMatrix.setToRotation(xAxis, delta.y / 5);
                point.mul(rotMatrix);
            }

            rotMatrix.setToRotation(yAxis, -delta.x / 5);
            point.mul(rotMatrix);

            cam.position.set(point.add(lookAt));
            cam.lookAt(lookAt.x, lookAt.y, lookAt.z);
        }
        if (mode == TransformMode.Zoom) {
            cam.fieldOfView -= -delta.y / 10;
        }
        if (mode == TransformMode.Translate) {
            tCurr.set(x, y);
            translated = true;
        }

        cam.update();
        last.set(x, y);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        cam.fieldOfView -= -amountY * Gdx.graphics.getDeltaTime() * 100;
        cam.update();
        return true;
    }

    enum TransformMode {
        Rotate, Translate, Zoom, None
    }
}
