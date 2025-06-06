package com.badlogic.gdx.tests.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.utils.GdxTest;

/**
 * The test shows the effects of TextureAttribute.Emissive and ColorAttribute.Emissive on a models Material.
 * <p>
 * Visually the test must show a rotating cube and background that have an orange glowing cross over the "badlogic" image. The
 * cross must be visible even when the cube side has rotated away from the light.
 */
public class MaterialEmissiveTest extends GdxTest {

    float angleY = 0;

    Model model, backModel;
    ModelInstance modelInstance;
    ModelInstance background;
    ModelBatch modelBatch;

    Environment environment;

    TextureAttribute diffuseTextureAttribute;
    TextureAttribute emissiveTextureAttribute;
    ColorAttribute emissiveColorAttribute;
    BlendingAttribute blendingAttribute;

    Material material;

    Texture diffuseTexture;
    Texture emissiveTexture;

    Camera camera;
    private float counter = 0.f;

    @Override
    public void create() {
        diffuseTexture = new Texture(Gdx.files.internal("data/badlogic.jpg"), true);
        emissiveTexture = new Texture(Gdx.files.internal("data/particle-star.png"), true);

        // Create material attributes. Each material can contain x-number of attributes.
        diffuseTextureAttribute = new TextureAttribute(TextureAttribute.Diffuse, diffuseTexture);
        emissiveTextureAttribute = new TextureAttribute(TextureAttribute.Emissive, emissiveTexture);
        emissiveColorAttribute = new ColorAttribute(ColorAttribute.Emissive, Color.ORANGE);
        blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        Material material = new Material(diffuseTextureAttribute, emissiveTextureAttribute, emissiveColorAttribute);

        ModelBuilder builder = new ModelBuilder();
        model = builder.createBox(1, 1, 1, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
        model.manageDisposable(diffuseTexture);
        model.manageDisposable(emissiveTexture);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.rotate(Vector3.X, 45);

        builder.begin();
        MeshPartBuilder mpb = builder.part("back", GL20.GL_TRIANGLES, Usage.Position | Usage.TextureCoordinates, material);
        mpb.rect(-2, -2, -2, 2, -2, -2, 2, 2, -2, -2, 2, -2, 0, 0, 1);
        backModel = builder.end();
        background = new ModelInstance(backModel);

        modelBatch = new ModelBatch();

        environment = new Environment();
        float ambientLight = 0.1f;
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientLight, ambientLight, ambientLight, 1f));
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.set(Color.WHITE, 10f, 0f, -5f);
        environment.add(directionalLight);

        camera = new PerspectiveCamera(45, 4, 4);
        camera.position.set(0, 0, 3);
        camera.direction.set(0, 0, -1);
        camera.update();

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render() {
        counter = (counter + Gdx.graphics.getDeltaTime()) % 1.f;
        blendingAttribute.opacity = 0.25f + Math.abs(0.5f - counter);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelInstance.transform.rotate(Vector3.Y, 30 * Gdx.graphics.getDeltaTime());
        modelBatch.begin(camera);
        modelBatch.render(background);
        modelBatch.render(modelInstance, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        model.dispose();
        backModel.dispose();
        modelBatch.dispose();
    }
}
