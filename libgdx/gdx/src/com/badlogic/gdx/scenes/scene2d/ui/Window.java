package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import org.jetbrains.annotations.Nullable;

/**
 * A table that can be dragged and act as a modal window. The top padding is used as the window's title height.
 * <p>
 * The preferred size of a window is the preferred size of the title text and the children as laid out by the table. After adding
 * children to the window, it can be convenient to call {@link #pack()} to size the window to the size of the children.
 */
public class Window extends Table {
    static private final Vector2 tmpPosition = new Vector2();
    static private final Vector2 tmpSize = new Vector2();
    static private final int MOVE = 1 << 5;
    protected int edge;
    protected boolean dragging;
    boolean isMovable = true, isModal, isResizable;
    int resizeBorder = 8;
    boolean keepWithinStage = true;
    Label titleLabel;
    Table titleTable;
    boolean drawTitleTable;
    private WindowStyle style;

    public Window(String title, Skin skin) {
        this(title, skin.get(WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title, Skin skin, String styleName) {
        this(title, skin.get(styleName, WindowStyle.class));
        setSkin(skin);
    }

    public Window(String title, WindowStyle style) {
        if (title == null) throw new IllegalArgumentException("title cannot be null.");
        setTouchable(Touchable.enabled);
        setClip(true);

        titleLabel = newLabel(title, new LabelStyle(style.titleFont, style.titleFontColor));
        titleLabel.setEllipsis(true);

        titleTable = new Table() {
            public void draw(Batch batch, float parentAlpha) {
                if (drawTitleTable) super.draw(batch, parentAlpha);
            }
        };
        titleTable.add(titleLabel).expandX().fillX().minWidth(0);
        addActor(titleTable);

        setStyle(style);
        setWidth(150);
        setHeight(150);

        addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toFront();
                return false;
            }
        });
        addListener(new InputListener() {
            float startX, startY, lastX, lastY;

            private void updateEdge(float x, float y) {
                float border = resizeBorder / 2f;
                float width = getWidth(), height = getHeight();
                float padTop = getPadTop(), padRight = getPadRight();
                float left = getPadLeft(), right = width - padRight, bottom = getPadBottom();
                edge = 0;
                if (isResizable && x >= left - border && x <= right + border && y >= bottom - border) {
                    if (x < left + border) edge |= Align.left;
                    if (x > right - border) edge |= Align.right;
                    if (y < bottom + border) edge |= Align.bottom;
                    if (edge != 0) border += 25;
                    if (x < left + border) edge |= Align.left;
                    if (x > right - border) edge |= Align.right;
                    if (y < bottom + border) edge |= Align.bottom;
                }
                if (isMovable && edge == 0 && y <= height && y >= height - padTop && x >= left && x <= right)
                    edge = MOVE;
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == 0) {
                    updateEdge(x, y);
                    dragging = edge != 0;
                    startX = x;
                    startY = y;
                    lastX = x - getWidth();
                    lastY = y - getHeight();
                }
                return edge != 0 || isModal;
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dragging = false;
            }

            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (!dragging) return;
                float width = getWidth(), height = getHeight();
                float windowX = getX(), windowY = getY();

                float minWidth = getMinWidth(), maxWidth = getMaxWidth();
                float minHeight = getMinHeight(), maxHeight = getMaxHeight();
                Stage stage = getStage();
                boolean clampPosition = keepWithinStage && stage != null && getParent() == stage.getRoot();

                if ((edge & MOVE) != 0) {
                    float amountX = x - startX, amountY = y - startY;
                    windowX += amountX;
                    windowY += amountY;
                }
                if ((edge & Align.left) != 0) {
                    float amountX = x - startX;
                    if (width - amountX < minWidth) amountX = -(minWidth - width);
                    if (clampPosition && windowX + amountX < 0) amountX = -windowX;
                    width -= amountX;
                    windowX += amountX;
                }
                if ((edge & Align.bottom) != 0) {
                    float amountY = y - startY;
                    if (height - amountY < minHeight) amountY = -(minHeight - height);
                    if (clampPosition && windowY + amountY < 0) amountY = -windowY;
                    height -= amountY;
                    windowY += amountY;
                }
                if ((edge & Align.right) != 0) {
                    float amountX = x - lastX - width;
                    if (width + amountX < minWidth) amountX = minWidth - width;
                    if (clampPosition && windowX + width + amountX > stage.getWidth())
                        amountX = stage.getWidth() - windowX - width;
                    width += amountX;
                }
                if ((edge & Align.top) != 0) {
                    float amountY = y - lastY - height;
                    if (height + amountY < minHeight) amountY = minHeight - height;
                    if (clampPosition && windowY + height + amountY > stage.getHeight())
                        amountY = stage.getHeight() - windowY - height;
                    height += amountY;
                }
                setBounds(Math.round(windowX), Math.round(windowY), Math.round(width), Math.round(height));
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                updateEdge(x, y);
                return isModal;
            }

            public boolean scrolled(InputEvent event, float x, float y, int amount) {
                return isModal;
            }

            public boolean keyDown(InputEvent event, int keycode) {
                return isModal;
            }

            public boolean keyUp(InputEvent event, int keycode) {
                return isModal;
            }

            public boolean keyTyped(InputEvent event, char character) {
                return isModal;
            }
        });
    }

    protected Label newLabel(String text, LabelStyle style) {
        return new Label(text, style);
    }

    /**
     * Returns the window's style. Modifying the returned style may not have an effect until {@link #setStyle(WindowStyle)} is
     * called.
     */
    public WindowStyle getStyle() {
        return style;
    }

    public void setStyle(WindowStyle style) {
        if (style == null) throw new IllegalArgumentException("style cannot be null.");
        this.style = style;

        setBackground(style.background);
        titleLabel.setStyle(new LabelStyle(style.titleFont, style.titleFontColor));
        invalidateHierarchy();
    }

    public void keepWithinStage() {
        if (!keepWithinStage) return;
        Stage stage = getStage();
        if (stage == null) return;
        Camera camera = stage.getCamera();
        if (camera instanceof OrthographicCamera) {
            OrthographicCamera orthographicCamera = (OrthographicCamera) camera;
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (getX(Align.right) - camera.position.x > parentWidth / 2 / orthographicCamera.zoom)
                setPosition(camera.position.x + parentWidth / 2 / orthographicCamera.zoom, getY(Align.right), Align.right);
            if (getX(Align.left) - camera.position.x < -parentWidth / 2 / orthographicCamera.zoom)
                setPosition(camera.position.x - parentWidth / 2 / orthographicCamera.zoom, getY(Align.left), Align.left);
            if (getY(Align.top) - camera.position.y > parentHeight / 2 / orthographicCamera.zoom)
                setPosition(getX(Align.top), camera.position.y + parentHeight / 2 / orthographicCamera.zoom, Align.top);
            if (getY(Align.bottom) - camera.position.y < -parentHeight / 2 / orthographicCamera.zoom)
                setPosition(getX(Align.bottom), camera.position.y - parentHeight / 2 / orthographicCamera.zoom, Align.bottom);
        } else if (getParent() == stage.getRoot()) {
            float parentWidth = stage.getWidth();
            float parentHeight = stage.getHeight();
            if (getX() < 0) setX(0);
            if (getRight() > parentWidth) setX(parentWidth - getWidth());
            if (getY() < 0) setY(0);
            if (getTop() > parentHeight) setY(parentHeight - getHeight());
        }
    }

    public void draw(Batch batch, float parentAlpha) {
        Stage stage = getStage();
        if (stage != null) {
            if (stage.getKeyboardFocus() == null) stage.setKeyboardFocus(this);

            keepWithinStage();

            if (style.stageBackground != null) {
                stageToLocalCoordinates(tmpPosition.set(0, 0));
                stageToLocalCoordinates(tmpSize.set(stage.getWidth(), stage.getHeight()));
                drawStageBackground(batch, parentAlpha, getX() + tmpPosition.x, getY() + tmpPosition.y, getX() + tmpSize.x,
                        getY() + tmpSize.y);
            }
        }
        super.draw(batch, parentAlpha);
    }

    protected void drawStageBackground(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        style.stageBackground.draw(batch, x, y, width, height);
    }

    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        super.drawBackground(batch, parentAlpha, x, y);

        // Manually draw the title table before clipping is done.
        titleTable.getColor().a = getColor().a;
        float padTop = getPadTop(), padLeft = getPadLeft();
        titleTable.setSize(getWidth() - padLeft - getPadRight(), padTop);
        titleTable.setPosition(padLeft, getHeight() - padTop);
        drawTitleTable = true;
        titleTable.draw(batch, parentAlpha);
        drawTitleTable = false; // Avoid drawing the title table again in drawChildren.
    }

    public @Nullable Actor hit(float x, float y, boolean touchable) {
        if (!isVisible()) return null;
        Actor hit = super.hit(x, y, touchable);
        if (hit == null && isModal && (!touchable || getTouchable() == Touchable.enabled)) return this;
        float height = getHeight();
        if (hit == null || hit == this) return hit;
        if (y <= height && y >= height - getPadTop() && x >= 0 && x <= getWidth()) {
            // Hit the title bar, don't use the hit child if it is in the Window's table.
            Actor current = hit;
            while (current.getParent() != this)
                current = current.getParent();
            if (getCell(current) != null) return this;
        }
        return hit;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean isModal() {
        return isModal;
    }

    public void setModal(boolean isModal) {
        this.isModal = isModal;
    }

    public void setKeepWithinStage(boolean keepWithinStage) {
        this.keepWithinStage = keepWithinStage;
    }

    public boolean isResizable() {
        return isResizable;
    }

    public void setResizable(boolean isResizable) {
        this.isResizable = isResizable;
    }

    public void setResizeBorder(int resizeBorder) {
        this.resizeBorder = resizeBorder;
    }

    public boolean isDragging() {
        return dragging;
    }

    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), titleTable.getPrefWidth() + getPadLeft() + getPadRight());
    }

    public Table getTitleTable() {
        return titleTable;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    /**
     * The style for a window, see {@link Window}.
     */
    static public class WindowStyle {
        public @Nullable Drawable background;
        public BitmapFont titleFont;
        public @Nullable Color titleFontColor = new Color(1, 1, 1, 1);
        public @Nullable Drawable stageBackground;

        public WindowStyle() {
        }

        public WindowStyle(BitmapFont titleFont, Color titleFontColor, @Nullable Drawable background) {
            this.titleFont = titleFont;
            this.titleFontColor.set(titleFontColor);
            this.background = background;
        }

        public WindowStyle(WindowStyle style) {
            titleFont = style.titleFont;
            if (style.titleFontColor != null) titleFontColor = new Color(style.titleFontColor);
            background = style.background;
            stageBackground = style.stageBackground;
        }
    }
}
