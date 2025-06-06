package com.badlogic.gdx.graphics.g3d.decals;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * <p>
 * This class provides hooks which are invoked by {@link DecalBatch} to evaluate the group a sprite falls into, as well as to
 * adjust settings before and after rendering a group.
 * </p>
 * <p>
 * A group is identified by an integer. The {@link #beforeGroup(int, Array) beforeGroup()} method provides the strategy with a
 * list of all the decals, which are contained in the group itself, and will be rendered before the associated call to
 * {@link #afterGroup(int)}.<br/>
 * A call to {@code beforeGroup()} is always followed by a call to {@code afterGroup()}.<br/>
 * <b>Groups are always invoked based on their ascending int values</b>. Group -10 will be rendered before group -5, group -5
 * before group 0, group 0 before group 6 and so on.<br/>
 * The call order for a single flush is always {@code beforeGroups(), beforeGroup1(), afterGroup1(), ... beforeGroupN(),
 * afterGroupN(), afterGroups()}.
 * </p>
 * <p>
 * The contents of the {@code beforeGroup()} call can be modified at will to realize view frustum culling, material & depth
 * sorting, ... all based on the requirements of the current group. The batch itself does not change OpenGL settings except for
 * whichever changes are entailed {@link DecalMaterial#set()}. If the group requires a special shader, blending,
 * {@link #getGroupShader(int)} should return it so that DecalBatch can apply it while rendering the group.
 * </p>
 */
public interface GroupStrategy {
    /**
     * Returns the shader to be used for the group. Can be null in which case the GroupStrategy doesn't support GLES 2.0
     *
     * @param group the group
     * @return the {@link ShaderProgram}
     */
    ShaderProgram getGroupShader(int group);

    /**
     * Assigns a group to a decal
     *
     * @param decal Decal to assign group to
     * @return group assigned
     */
    int decideGroup(Decal decal);

    /**
     * Invoked directly before rendering the contents of a group
     *
     * @param group    Group that will be rendered
     * @param contents Array of entries of arrays containing all the decals in the group
     */
    void beforeGroup(int group, Array<Decal> contents);

    /**
     * Invoked directly after rendering of a group has completed
     *
     * @param group Group which completed rendering
     */
    void afterGroup(int group);

    /**
     * Invoked before rendering any group
     */
    void beforeGroups();

    /**
     * Invoked after having rendered all groups
     */
    void afterGroups();
}
