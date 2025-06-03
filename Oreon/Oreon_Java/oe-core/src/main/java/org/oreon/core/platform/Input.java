package org.oreon.core.platform;

import org.oreon.core.math.Vec2f;

public interface Input {

    void create(long windowId);

    void update();

    void shutdown();

    boolean isKeyPushed(int key);

    boolean isKeyHolding(int key);

    boolean isKeyReleased(int key);

    boolean isButtonPushed(int key);

    boolean isButtonHolding(int key);

    boolean isButtonReleased(int key);

    float getScrollOffset();

    Vec2f getCursorPosition();

    Vec2f getLockedCursorPosition();
}
