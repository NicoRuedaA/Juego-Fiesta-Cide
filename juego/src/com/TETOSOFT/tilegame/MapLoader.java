package com.TETOSOFT.tilegame;

import java.awt.GraphicsConfiguration;
import java.io.IOException;

import com.TETOSOFT.assets.AssetManager;
import com.TETOSOFT.assets.MapParser;
import com.TETOSOFT.assets.SpriteFactory;

/**
 * Manages map progression: tracks the current map number and delegates
 * actual parsing to {@link MapParser}.
 *
 * Image loading is handled by {@link AssetManager} and sprite construction
 * by {@link SpriteFactory}.
 */
public class MapLoader {

    public int currentMap = 0;

    private final AssetManager  assets;
    private final MapParser     parser;

    public MapLoader(GraphicsConfiguration gc) {
        assets = new AssetManager(gc);
        SpriteFactory spriteFactory = new SpriteFactory(assets);
        parser = new MapParser(assets, spriteFactory);
    }

    /** Returns an {@link AssetManager} so callers can load images (e.g. background). */
    public AssetManager getAssets() { return assets; }

    /** Advances to the next map and returns it, wrapping around to map 1 if needed. */
    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = parser.parse("maps/map" + currentMap + ".txt");
            } catch (IOException ex) {
                if (currentMap == 1) return null;   // no maps at all
                currentMap = 0;                     // wrap around
            }
        }
        return map;
    }

    /** Reloads the current map (used on player death). */
    public TileMap reloadMap() {
        try {
            return parser.parse("maps/map" + currentMap + ".txt");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
