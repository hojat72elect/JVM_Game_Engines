package org.openrndr.extra.meshgenerators

import org.openrndr.draw.VertexBuffer
import org.openrndr.math.Spherical
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import kotlin.math.sqrt

// Based on
// https://github.com/mrdoob/three.js/blob/master/src/geometries/DodecahedronGeometry.js

/**
 * A dodecahedron mesh
 *
 * @param radius the radius of the dodecahedron
 * @return A vertex buffer containing the triangles to render the 3D shape
 */
fun dodecahedronMesh(
    radius: Double = 1.0
): VertexBuffer {
    val vb = meshVertexBuffer(12 * 3 * 3)
    vb.put {
        generateDodecahedron(radius, bufferWriter(this))
    }
    return vb
}

/**
 * Generate dodecahedron mesh
 *
 * @param radius the radius of the dodecahedron
 * @param writer the vertex writer function
 */
fun generateDodecahedron(
    radius: Double = 1.0,
    writer: VertexWriter
) {

    val t = (1.0 + sqrt(5.0)) / 2
    val r = 1 / t

    val vertices = listOf(
        // (±1, ±1, ±1)
        -1.0, -1.0, -1.0, -1.0, -1.0, 1.0,
        -1.0, 1.0, -1.0, -1.0, 1.0, 1.0,
        1.0, -1.0, -1.0, 1.0, -1.0, 1.0,
        1.0, 1.0, -1.0, 1.0, 1.0, 1.0,

        // (0, ±1/φ, ±φ)
        0.0, -r, -t, 0.0, -r, t,
        0.0, r, -t, 0.0, r, t,

        // (±1/φ, ±φ, 0)
        -r, -t, 0.0, -r, t, 0.0,
        r, -t, 0.0, r, t, 0.0,

        // (±φ, 0, ±1/φ)
        -t, 0.0, -r, t, 0.0, -r,
        -t, 0.0, r, t, 0.0, r
    )

    val indices = listOf(
        3, 11, 7, 3, 7, 15, 3, 15, 13,
        7, 19, 17, 7, 17, 6, 7, 6, 15,
        17, 4, 8, 17, 8, 10, 17, 10, 6,
        8, 0, 16, 8, 16, 2, 8, 2, 10,
        0, 12, 1, 0, 1, 18, 0, 18, 16,
        6, 10, 2, 6, 2, 13, 6, 13, 15,
        2, 16, 18, 2, 18, 3, 2, 3, 13,
        18, 1, 9, 18, 9, 11, 18, 11, 3,
        4, 14, 12, 4, 12, 0, 4, 0, 8,
        11, 9, 5, 11, 5, 19, 11, 19, 7,
        19, 5, 14, 19, 14, 4, 19, 4, 17,
        1, 12, 14, 1, 14, 5, 1, 5, 9
    )

    val ii = indices.iterator()
    while (ii.hasNext()) {
        val tri = List(3) {
            val i = ii.next()
            Vector3(
                vertices[i * 3],
                vertices[i * 3 + 1],
                vertices[i * 3 + 2]
            ) * radius
        }
        val up = (tri[1] - tri[0]).cross(tri[2] - tri[0]).normalized

        writer(tri[0], up, tri[0].toUV())
        writer(tri[1], up, tri[1].toUV())
        writer(tri[2], up, tri[2].toUV())
    }
}

/**
 * Converts a 3D vertex to a 2D UV coordinate using a spherical wrapping.
 */
private fun Vector3.toUV(): Vector2 {
    val thetaMax = 180.0 * 2.0
    val phiMax = 180.0

    val spherical = Spherical.fromVector(this.normalized)
    return Vector2(spherical.theta / thetaMax + 0.5, 1.0 - spherical.phi / phiMax)
}
