/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.gde.scenecomposer.gizmo.shape;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author dokthar
 */
public class Triangle extends Mesh {

    private final float width;
    private final float height;

    public Triangle(float width, float height) {
        this.width = width;
        this.height = height;

        setGeometryData();
        setIndexData();
        
        updateBound();
    }

    private void setGeometryData() {
        setMode(Mode.Lines);

        FloatBuffer posBuf = BufferUtils.createVector3Buffer(3);
        FloatBuffer texBuf = BufferUtils.createVector2Buffer(3);

        setBuffer(VertexBuffer.Type.Position, 3, posBuf);
        setBuffer(VertexBuffer.Type.TexCoord, 2, texBuf);

        posBuf.put(-width).put(0).put(0);
        posBuf.put(0).put(height).put(0);
        posBuf.put(width).put(0).put(0);
        
        texBuf.put(-width).put(0);
        texBuf.put(0).put(height);
        texBuf.put(width).put(0);
    }

    private void setIndexData() {
        int nbSegments = 3;

        ShortBuffer idxBuf = BufferUtils.createShortBuffer(2 * nbSegments);
        setBuffer(VertexBuffer.Type.Index, 2, idxBuf);
        idxBuf.put((short) 0);
        idxBuf.put((short) 1);

        idxBuf.put((short) 1);
        idxBuf.put((short) 2);

        idxBuf.put((short) 2);
        idxBuf.put((short) 0);
    }
}
