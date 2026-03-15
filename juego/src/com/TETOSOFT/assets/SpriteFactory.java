package com.TETOSOFT.assets;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.TETOSOFT.graphics.Animation;
import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.sprites.*;

/**
 * Builds the prototype {@link Sprite} objects that {@link MapParser} clones
 * when populating a level.
 *
 * All image transforms (mirror, flip) are applied here so that the rest of
 * the code never needs to know about sprite sheet layout.
 */
public class SpriteFactory {

    private final AssetManager assets;

    // Prototype sprites — cloned for every instance placed in a map
    private final Sprite playerSprite;
    private final Sprite coinSprite;
    private final Sprite musicSprite;
    private final Sprite goalSprite;
    private final Sprite grubSprite;
    private final Sprite flySprite;
    private final Sprite variantFlySprite; // prototipo para VariantFly

    public SpriteFactory(AssetManager assets) {
        this.assets = assets;
        playerSprite     = buildPlayerSprite();
        flySprite        = buildFlySprite();
        grubSprite       = buildGrubSprite();
        variantFlySprite = buildVariantFlySprite();
        coinSprite       = buildCoinSprite();
        musicSprite      = buildMusicSprite();
        goalSprite       = buildGoalSprite();
    }

    // -------------------------------------------------------------------------
    // Public accessors (return clones so prototypes are never mutated)
    // -------------------------------------------------------------------------

    public Sprite getPlayer() {
        return (Sprite) playerSprite.clone();
    }

    public Sprite getCoin() {
        return (Sprite) coinSprite.clone();
    }

    public Sprite getMusic() {
        return (Sprite) musicSprite.clone();
    }

    public Sprite getGoal() {
        return (Sprite) goalSprite.clone();
    }

    public Sprite getGrub() {
        return (Sprite) grubSprite.clone();
    }

    public Sprite getFly() {
        return (Sprite) flySprite.clone();
    }

    public Sprite getVariantFly() {
        return (Sprite) variantFlySprite.clone();
    }

    /**
     * Crea un SpawnerGrub con el supplier de VariantFly ya inyectado.
     * No se clona desde prototipo porque el Supplier no es clonable.
     */
    public SpawnerGrub getSpawnerGrub() {
        Image g1 = assets.loadImage("enemy2.png");
        Image g2 = assets.loadImage("enemy22.png");
        Animation[] a = buildFourOrientations(
                createGrubAnim(g1, g2),
                createGrubAnim(assets.getMirrorImage(g1), assets.getMirrorImage(g2)),
                createGrubAnim(assets.getFlippedImage(g1), assets.getFlippedImage(g2)),
                createGrubAnim(assets.getFlippedImage(assets.getMirrorImage(g1)),
                        assets.getFlippedImage(assets.getMirrorImage(g2))));
        return new SpawnerGrub(a[0], a[1], a[2], a[3], this::getVariantFly);
    }

    // -------------------------------------------------------------------------
    // Sprite builders
    // -------------------------------------------------------------------------

    private Sprite buildPlayerSprite() {
        // El archivo en el JAR es player.PNG — AssetManager lo resuelve solo
        Image sheet = assets.loadImage("player.png");

        Animation right     = createPlayerAnim(sheet, false, false);
        Animation left      = createPlayerAnim(sheet, true,  false);
        Animation deadRight = createPlayerAnim(sheet, false, true);
        Animation deadLeft  = createPlayerAnim(sheet, true,  true);

        Player player = new Player(left, right, deadLeft, deadRight);
        player.setDuckImage(assets.loadImage("player_duck.png"));
        return player;
    }

    /**
     * Recorta los frames del sheet y opcionalmente los espejea/voltea frame a
     * frame, preservando siempre el orden original de los frames.
     *
     * Frame layout:
     *   0,1,2 → walk cycle
     *   3     → idle
     *   4     → jump
     */
    private Animation createPlayerAnim(Image playerSheet, boolean mirror, boolean flip) {
        final int TOTAL_FRAMES = 5;
        final int[] WALK_FRAMES = { 0, 1, 2 };
        final int IDLE_FRAME = 4;
        final int JUMP_FRAME = 3;

        int w = playerSheet.getWidth(null) / TOTAL_FRAMES;
        int h = playerSheet.getHeight(null);

        GraphicsConfiguration gc = assets.getGraphicsConfiguration();
        BufferedImage[] frames = new BufferedImage[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            BufferedImage raw = gc.createCompatibleImage(w, h, Transparency.BITMASK);
            Graphics g = raw.getGraphics();
            g.drawImage(playerSheet, 0, 0, w, h, i * w, 0, (i + 1) * w, h, null);
            g.dispose();

            Image transformed = raw;
            if (mirror) transformed = assets.getMirrorImage(transformed);
            if (flip)   transformed = assets.getFlippedImage(transformed);

            if (transformed instanceof BufferedImage) {
                frames[i] = (BufferedImage) transformed;
            } else {
                frames[i] = gc.createCompatibleImage(w, h, Transparency.BITMASK);
                Graphics g2 = frames[i].getGraphics();
                g2.drawImage(transformed, 0, 0, null);
                g2.dispose();
            }
        }

        Animation anim = new Animation();
        for (int i : WALK_FRAMES) anim.addFrame(frames[i], 100);
        anim.addFrame(frames[IDLE_FRAME], 100);
        anim.addFrame(frames[JUMP_FRAME], 100);
        return anim;
    }

    private Sprite buildFlySprite() {
        Image i1 = assets.loadImage("projectile1_1.png");
        Image i2 = assets.loadImage("projectile1_2.png");
        Image i3 = assets.loadImage("projectile1_3.png");

        Animation[] a = buildFourOrientations(
                createFlyAnim(i1, i2, i3),
                createFlyAnim(assets.getMirrorImage(i1), assets.getMirrorImage(i2), assets.getMirrorImage(i3)),
                createFlyAnim(assets.getFlippedImage(i1), assets.getFlippedImage(i2), assets.getFlippedImage(i3)),
                createFlyAnim(assets.getFlippedImage(assets.getMirrorImage(i1)),
                        assets.getFlippedImage(assets.getMirrorImage(i2)),
                        assets.getFlippedImage(assets.getMirrorImage(i3))));
        return new Fly(a[0], a[1], a[2], a[3]);
    }

    /** VariantFly usa las mismas imagenes que Fly pero instancia VariantFly. */
    private Sprite buildVariantFlySprite() {
        Image i1 = assets.loadImage("projectile2_1.png");
        Image i2 = assets.loadImage("projectile2_1.png");
        Image i3 = assets.loadImage("projectile2_1.png");

        Animation[] a = buildFourOrientations(
                createFlyAnim(i1, i2, i3),
                createFlyAnim(assets.getMirrorImage(i1), assets.getMirrorImage(i2), assets.getMirrorImage(i3)),
                createFlyAnim(assets.getFlippedImage(i1), assets.getFlippedImage(i2), assets.getFlippedImage(i3)),
                createFlyAnim(assets.getFlippedImage(assets.getMirrorImage(i1)),
                        assets.getFlippedImage(assets.getMirrorImage(i2)),
                        assets.getFlippedImage(assets.getMirrorImage(i3))));
        return new VariantFly(a[0], a[1], a[2], a[3]);
    }

    private Animation createFlyAnim(Image i1, Image i2, Image i3) {
        Animation anim = new Animation();
        anim.addFrame(i1, 50);
        anim.addFrame(i2, 50);
        anim.addFrame(i3, 50);
        anim.addFrame(i2, 50);
        return anim;
    }

    private Sprite buildGrubSprite() {
        Image g1 = assets.loadImage("grub1.png");
        Image g2 = assets.loadImage("grub2.png");

        Animation[] a = buildFourOrientations(
                createGrubAnim(g1, g2),
                createGrubAnim(assets.getMirrorImage(g1), assets.getMirrorImage(g2)),
                createGrubAnim(assets.getFlippedImage(g1), assets.getFlippedImage(g2)),
                createGrubAnim(assets.getFlippedImage(assets.getMirrorImage(g1)),
                        assets.getFlippedImage(assets.getMirrorImage(g2))));
        return new Grub(a[0], a[1], a[2], a[3]);
    }

    private Animation createGrubAnim(Image i1, Image i2) {
        Animation anim = new Animation();
        anim.addFrame(i1, 100);
        anim.addFrame(i2, 100);
        return anim;
    }

    private Sprite buildCoinSprite() {
        Animation anim = new Animation();
        // Los archivos en el JAR son coin1.PNG...coin5.PNG — AssetManager los resuelve
        for (int i = 1; i <= 5; i++)
            anim.addFrame(assets.loadImage("coin" + i + ".png"), 250);
        return new PowerUp.Star(anim);
    }

    private Sprite buildMusicSprite() {
        Animation anim = new Animation();
        // En el JAR no existen music1-3.png; se usan star1-3.PNG como fallback visual
        for (int i = 1; i <= 3; i++)
            anim.addFrame(assets.loadImage("star" + i + ".png"), 150);
        return new PowerUp.Music(anim);
    }

    private Sprite buildGoalSprite() {
        Animation anim = new Animation();
        anim.addFrame(assets.loadImage("heart.png"), 150);
        return new PowerUp.Goal(anim);
    }

    // -------------------------------------------------------------------------
    // Helper: package four pre-built animations into an array [left,right,dL,dR]
    // -------------------------------------------------------------------------

    private Animation[] buildFourOrientations(Animation left, Animation right,
            Animation deadLeft, Animation deadRight) {
        return new Animation[] { left, right, deadLeft, deadRight };
    }
}