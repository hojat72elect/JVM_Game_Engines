package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tests.utils.GdxTest;

public class SimpleAnimationTest extends GdxTest {
    private static final float ANIMATION_SPEED = 0.2f;
    private Animation<TextureRegion> currentWalk;
    private float currentFrameTime;
    private Vector2 position;
    private Texture texture;
    private Animation<TextureRegion> downWalk;
    private Animation<TextureRegion> leftWalk;
    private Animation<TextureRegion> rightWalk;
    private Animation<TextureRegion> upWalk;
    private SpriteBatch spriteBatch;

    @Override
    public void create() {
        Gdx.input.setInputProcessor(this);
        texture = new Texture(Gdx.files.internal("data/animation.png"));
        TextureRegion[][] regions = TextureRegion.split(texture, 32, 48);
        TextureRegion[] downWalkReg = regions[0];
        TextureRegion[] leftWalkReg = regions[1];
        TextureRegion[] rightWalkReg = regions[2];
        TextureRegion[] upWalkReg = regions[3];
        downWalk = new Animation<TextureRegion>(ANIMATION_SPEED, downWalkReg);
        leftWalk = new Animation<TextureRegion>(ANIMATION_SPEED, leftWalkReg);
        rightWalk = new Animation<TextureRegion>(ANIMATION_SPEED, rightWalkReg);
        upWalk = new Animation<TextureRegion>(ANIMATION_SPEED, upWalkReg);

        currentWalk = leftWalk;
        currentFrameTime = 0.0f;

        spriteBatch = new SpriteBatch();
        position = new Vector2();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        currentFrameTime += Gdx.graphics.getDeltaTime();

        spriteBatch.begin();
        TextureRegion frame = currentWalk.getKeyFrame(currentFrameTime, true);
        spriteBatch.draw(frame, position.x, position.y);
        spriteBatch.end();
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        position.x = x;
        position.y = Gdx.graphics.getHeight() - y;
        return true;
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        texture.dispose();
    }
}
