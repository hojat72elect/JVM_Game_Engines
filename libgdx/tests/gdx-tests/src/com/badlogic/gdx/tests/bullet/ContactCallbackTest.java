package com.badlogic.gdx.tests.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.utils.Array;


public class ContactCallbackTest extends BaseBulletTest {
    final int BOXCOUNT_X = 5;
    final int BOXCOUNT_Y = 1;
    final int BOXCOUNT_Z = 5;
    final float BOXOFFSET_X = -5f;
    final float BOXOFFSET_Y = 0.5f;
    final float BOXOFFSET_Z = -5f;
    TestContactProcessedListener contactProcessedListener;

    @Override
    public void create() {
        super.create();

        // Create the entities
        world.add("ground", 0f, 0f, 0f).setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);

        for (int x = 0; x < BOXCOUNT_X; x++) {
            for (int y = 0; y < BOXCOUNT_Y; y++) {
                for (int z = 0; z < BOXCOUNT_Z; z++) {
                    final BulletEntity e = world.add("box", BOXOFFSET_X + x * 2f, BOXOFFSET_Y + y * 2f,
                            BOXOFFSET_Z + z * 2f);
                    e.setColor(0.5f + 0.5f * (float) Math.random(), 0.5f + 0.5f * (float) Math.random(),
                            0.5f + 0.5f * (float) Math.random(), 1f);

                    e.body.setContactCallbackFlag(2);
                    e.body.setContactCallbackFilter(2);
                }
            }
        }

        // Creating a contact listener, also enables that particular type of contact listener and sets it active.
        contactProcessedListener = new TestContactProcessedListener();
        contactProcessedListener.entities = world.entities;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        shoot(x, y);
        return true;
    }

    @Override
    public void dispose() {
        // Deleting the active contact listener, also disables that particular type of contact listener.
        if (contactProcessedListener != null) contactProcessedListener.dispose();
        contactProcessedListener = null;
        super.dispose();
    }

    // ContactProcessedListenerXXX is called AFTER the contact is processed.
    // Use ContactAddedListenerXXX to get a callback BEFORE the contact processed,
    // which allows you to alter the objects/manifold before it's processed.
    public static class TestContactProcessedListener extends ContactListener {
        public Array<BulletEntity> entities;
        int c = 0;

        @Override
        public void onContactProcessed(int userValue0, boolean match0, int userValue1, boolean match1) {
            if (match0) {
                final BulletEntity e = entities.get(userValue0);
                // Disable future callbacks for this entity
                e.body.setContactCallbackFilter(0);
                e.setColor(Color.RED);
                Gdx.app.log("ContactCallbackTest", "Contact processed " + (++c));
            }
            if (match1) {
                final BulletEntity e = entities.get(userValue1);
                // Disable future callbacks for this entity
                e.body.setContactCallbackFilter(0);
                e.setColor(Color.RED);
                Gdx.app.log("ContactCallbackTest", "Contact processed " + (++c));
            }
        }
    }
}
