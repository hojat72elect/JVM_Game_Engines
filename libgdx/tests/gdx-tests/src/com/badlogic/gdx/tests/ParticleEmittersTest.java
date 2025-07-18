package com.badlogic.gdx.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class ParticleEmittersTest extends GdxTest {
    ParticleEffect effect;
    ParticleEffectPool effectPool;
    Array<PooledEffect> effects = new Array();
    PooledEffect latestEffect;
    float fpsCounter;
    Stage ui;
    CheckBox skipCleanup;
    Button clearEmitters, scaleEffects;
    Label logLabel;
    ChangeListener listener = new ChangeListener() {

        @Override
        public void changed(ChangeEvent event, Actor actor) {
            if (actor == skipCleanup) {
                updateSkipCleanupState();
            } else if (actor == clearEmitters) {
                for (PooledEffect e : effects)
                    e.free();
                effects.clear();
            } else if (actor == scaleEffects) {
                for (ParticleEffect eff : effects) {
                    eff.scaleEffect(1.5f);
                }
            }
        }
    };
    private SpriteBatch spriteBatch;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("data/singleTextureAllAdditive.p"), Gdx.files.internal("data"));
        effect.setPosition(Gdx.graphics.getWidth() / 2F, Gdx.graphics.getHeight() / 2F);
        effectPool = new ParticleEffectPool(effect, 20, 20);

        setupUI();

        InputProcessor inputProcessor = new InputAdapter() {

            public boolean touchDragged(int x, int y, int pointer) {
                if (latestEffect != null) latestEffect.setPosition(x, Gdx.graphics.getHeight() - y);
                return false;
            }

            public boolean touchDown(int x, int y, int pointer, int newParam) {
                latestEffect = effectPool.obtain();
                latestEffect.setEmittersCleanUpBlendFunction(!skipCleanup.isChecked());
                latestEffect.setPosition(x, Gdx.graphics.getHeight() - y);
                effects.add(latestEffect);

                return false;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui);
        multiplexer.addProcessor(inputProcessor);

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        effect.dispose();
    }

    @Override
    public void resize(int width, int height) {
        ui.getViewport().update(width, height);
    }

    public void render() {
        ui.act();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        for (ParticleEffect e : effects)
            e.draw(spriteBatch, delta);
        spriteBatch.end();
        fpsCounter += delta;
        if (fpsCounter > 3) {
            fpsCounter = 0;
            String log = effects.size + " particle effects, FPS: " + Gdx.graphics.getFramesPerSecond() + ", Render calls: "
                    + spriteBatch.renderCalls;
            Gdx.app.log("libGDX", log);
            logLabel.setText(log);
        }
        ui.draw();
    }

    public boolean needsGL20() {
        return false;
    }

    private void setupUI() {
        ui = new Stage(new ExtendViewport(640, 480));
        Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        skipCleanup = new CheckBox("Skip blend function clean-up", skin);
        skipCleanup.addListener(listener);
        logLabel = new Label("", skin.get(LabelStyle.class));
        clearEmitters = new TextButton("Clear screen", skin);
        clearEmitters.addListener(listener);
        scaleEffects = new TextButton("Scale existing effects", skin);
        scaleEffects.addListener(listener);
        Table table = new Table();
        table.setTransform(false);
        table.setFillParent(true);
        table.defaults().padTop(5).left();
        table.top().left().padLeft(5);
        table.add(skipCleanup).colspan(2).row();
        table.add(clearEmitters).spaceRight(10);
        table.add(scaleEffects).row();
        table.add(logLabel).colspan(2);
        ui.addActor(table);
    }

    void updateSkipCleanupState() {
        for (ParticleEffect eff : effects) {
            for (ParticleEmitter e : eff.getEmitters())
                e.setCleansUpBlendFunction(!skipCleanup.isChecked());
        }
    }
}
