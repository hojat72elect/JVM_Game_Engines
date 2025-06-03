package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapRenderer;

public interface TiledMapRenderer extends MapRenderer {
    void renderObjects(MapLayer layer);

    void renderObject(MapObject object);

    void renderTileLayer(TiledMapTileLayer layer);

    void renderImageLayer(TiledMapImageLayer layer);
}
