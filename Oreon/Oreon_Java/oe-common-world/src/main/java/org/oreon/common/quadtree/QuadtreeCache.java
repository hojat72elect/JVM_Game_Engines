package org.oreon.common.quadtree;

import lombok.Getter;
import org.oreon.core.scenegraph.Node;

import java.util.HashMap;

public class QuadtreeCache {

    @Getter
    private final HashMap<String, QuadtreeNode> chunks;

    public QuadtreeCache() {

        chunks = new HashMap<String, QuadtreeNode>();
    }

    public boolean contains(String key) {

        return chunks.containsKey(key);
    }

    public void addChunk(Node chunk) {

        chunks.put(((QuadtreeNode) chunk).getQuadtreeCacheKey(), (QuadtreeNode) chunk);
    }

    public void addChunk(QuadtreeNode chunk) {

        chunks.put(chunk.getQuadtreeCacheKey(), chunk);
    }

    public QuadtreeNode getChunk(String key) {

        return chunks.get(key);
    }

    public void removeChunk(String key) {

        chunks.remove(key);
    }

    public QuadtreeNode getAndRemoveChunk(String key) {

        QuadtreeNode chunk = chunks.get(key);
        chunks.remove(key);
        return chunk;
    }
}
