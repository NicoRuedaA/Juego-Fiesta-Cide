package com.TETOSOFT.assets;

import java.awt.Image;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.TileMap;
import com.TETOSOFT.tilegame.TileMapDrawer;

/**
 * Reads a text-based map file and produces a populated {@link TileMap}.
 *
 * <h3>Map file format</h3>
 * <ul>
 *   <li>Lines starting with {@code #} are comments.</li>
 *   <li>Upper-case letters A–Z refer to tile images (A.png, B.png …).</li>
 *   <li>Special characters place sprites (see {@link #buildSpriteMap}).</li>
 * </ul>
 */
public class MapParser {

    private final AssetManager  assets;
    private final SpriteFactory spriteFactory;

    /** Tile images indexed by letter (index 0 = 'A'). */
    private final List<Image> tileImages = new ArrayList<>();

    /**
     * Maps a map-file character to the prototype sprite that should be placed
     * at that position.  Adding a new entity only requires one line here.
     */
    private final Map<Character, Sprite> spriteMap;

    public MapParser(AssetManager assets, SpriteFactory spriteFactory) {
        this.assets        = assets;
        this.spriteFactory = spriteFactory;
        loadTileImages();
        spriteMap = buildSpriteMap();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Parses the map file at {@code path} and returns a fully populated
     * {@link TileMap}, or throws {@link IOException} if the file cannot be read.
     */
    public TileMap parse(String path) throws IOException {
        List<String> lines = readLines(path);
        int width  = lines.stream().mapToInt(String::length).max().orElse(0);
        int height = lines.size();

        TileMap map = new TileMap(width, height);

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch   = line.charAt(x);
                int  tile = ch - 'A';

                if (tile >= 0 && tile < tileImages.size()) {
                    map.setTile(x, y, tileImages.get(tile));
                } else if (spriteMap.containsKey(ch)) {
                    placeSprite(map, spriteMap.get(ch), x, y);
                }
            }
        }

        Sprite player = spriteFactory.getPlayer();
        player.setX(TileMapDrawer.tilesToPixels(3));
        player.setY(height);
        map.setPlayer(player);

        return map;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Defines which character in the map file spawns which sprite type.
     * To add a new entity: add one entry here.
     */
    private Map<Character, Sprite> buildSpriteMap() {
        Map<Character, Sprite> map = new HashMap<>();
        map.put('o', spriteFactory.getCoin());
        map.put('!', spriteFactory.getMusic());
        map.put('*', spriteFactory.getGoal());
        map.put('1', spriteFactory.getGrub());
        map.put('2', spriteFactory.getFly());
        map.put('3', spriteFactory.getSpawnerGrub()); // SpawnerGrub — instancia VariantFly cada 5s
        return map;
    }

    private void loadTileImages() {
        char ch = 'A';
        while (true) {
            String name = ch + ".png";
            if (!new File("images/" + name).exists()) break;
            tileImages.add(assets.loadImage(name));
            ch++;
        }
    }

    private List<String> readLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) lines.add(line);
            }
        }
        return lines;
    }

    /** Clones a prototype sprite and positions it on the given tile. */
    private void placeSprite(TileMap map, Sprite prototype, int tileX, int tileY) {
        Sprite sprite = (Sprite) prototype.clone();
        int pixX = TileMapDrawer.tilesToPixels(tileX)
                 + (TileMapDrawer.tilesToPixels(1) - sprite.getWidth()) / 2;
        int pixY = TileMapDrawer.tilesToPixels(tileY + 1) - sprite.getHeight();
        sprite.setX(pixX);
        sprite.setY(pixY);
        map.addSprite(sprite);
    }
}
