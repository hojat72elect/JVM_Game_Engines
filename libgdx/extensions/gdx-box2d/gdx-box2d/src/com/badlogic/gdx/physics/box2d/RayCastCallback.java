package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

/**
 * Callback class for ray casts.
 *
 * @see World#rayCast(RayCastCallback, Vector2, Vector2)
 */

public interface RayCastCallback {

    /**
     * Called for each fixture found in the query. You control how the ray cast proceeds by returning a float: return -1: ignore
     * this fixture and continue return 0: terminate the ray cast return fraction: clip the ray to this point return 1: don't clip
     * the ray and continue.
     * <p>
     * The {@link Vector2} instances passed to the callback will be reused for future calls so make a copy of them!
     *
     * @param fixture the fixture hit by the ray
     * @param point   the point of initial intersection
     * @param normal  the normal vector at the point of intersection
     * @return -1 to filter, 0 to terminate, fraction to clip the ray for closest hit, 1 to continue
     **/
    float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction);
}
