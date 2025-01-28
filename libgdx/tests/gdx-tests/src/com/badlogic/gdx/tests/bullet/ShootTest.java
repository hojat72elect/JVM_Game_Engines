package com.badlogic.gdx.tests.bullet;


public class ShootTest extends BaseBulletTest {
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 5;
    final int BOXCOUNT_Z = 1;

    final float BOXOFFSET_X = -2.5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = 0f;

    BulletEntity ground;

    @Override
    public void create() {
        super.create();

        // Create the entities
        (ground = world.add("ground", 0f, 0f, 0f)).setColor(0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(), 1f);

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    world.add("box", BOXOFFSET_X + x, BOXOFFSET_Y + y, BOXOFFSET_Z + z).setColor(0.5f + 0.5f * (float) Math.random(),
                            0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(), 1f);
                }
            }
        }
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public void dispose() {
        super.dispose();
        ground = null;
    }
}
