package com.TETOSOFT.tilegame;

import java.awt.*;
import java.util.Iterator;

import com.TETOSOFT.core.GameConstants;
import com.TETOSOFT.core.GameCore;
import com.TETOSOFT.graphics.Sprite;
import com.TETOSOFT.tilegame.sprites.Creature;
import com.TETOSOFT.tilegame.sprites.Player;
import com.TETOSOFT.tilegame.sprites.SpawnerGrub;
import com.TETOSOFT.tilegame.systems.*;

public class GameEngine extends GameCore implements CollisionSystem.Listener {

    public static void main(String[] args) {
        new GameEngine().run();
    }

    // -------------------------------------------------------------------------
    // Estados de pantalla
    // -------------------------------------------------------------------------

    private enum GameState { MAIN_MENU, PLAYING, PAUSED, GAME_OVER }

    private GameState state = GameState.MAIN_MENU;

    // Botón seleccionado en los menús
    private int menuSelection = 0;

    // -------------------------------------------------------------------------
    // Game state
    // -------------------------------------------------------------------------

    private int lives = GameConstants.STARTING_LIVES;
    private int coins = 0;
    private boolean debugHitboxes = false;

    // -------------------------------------------------------------------------
    // Subsystems
    // -------------------------------------------------------------------------

    private TileMap          map;
    private MapLoader        mapLoader;
    private TileMapDrawer    drawer;
    private PhysicsSystem    physics;
    private CollisionSystem  collision;
    private PlayerController controller;
    private HudRenderer      hud;
    private MenuRenderer     menuRenderer;
    private MenuController   menuController;

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void init() {
        super.init();

        mapLoader      = new MapLoader(screen.getWindow().getGraphicsConfiguration());
        drawer         = new TileMapDrawer();
        physics        = new PhysicsSystem();
        collision      = new CollisionSystem(this);
        controller     = new PlayerController();
        hud            = new HudRenderer();
        menuRenderer   = new MenuRenderer();
        menuController = new MenuController();

        controller.init(screen.getWindow());
        menuController.init(screen.getWindow());

        physics.setBlockHitListener((creature, tx, ty) -> map.breakTile(tx, ty));

        drawer.addParallaxLayer(assets().loadImage("bg_layer1.png"), 0.0f);
        drawer.addParallaxLayer(assets().loadImage("bg_layer2.png"), 0.2f);
        drawer.addParallaxLayer(assets().loadImage("bg_layer3.png"), 0.4f);
        drawer.addParallaxLayer(assets().loadImage("bg_layer4.png"), 0.7f);
    }

    // -------------------------------------------------------------------------
    // Game loop — update
    // -------------------------------------------------------------------------

    @Override
    public void update(long elapsedTime) {
        switch (state) {
            case MAIN_MENU: updateMainMenu(); break;
            case PLAYING:   updatePlaying(elapsedTime); break;
            case PAUSED:    updatePaused(); break;
            case GAME_OVER: updateGameOver(); break;
        }
    }

    private void updateMainMenu() {
        // Navegación: 3 botones (Jugar, Ajustes, Salir)
        if (menuController.isUpPressed())   menuSelection = Math.max(0, menuSelection - 1);
        if (menuController.isDownPressed()) menuSelection = Math.min(2, menuSelection + 1);

        if (menuController.isEnterPressed()) {
            switch (menuSelection) {
                case 0: startGame();      break; // Jugar
                case 1: /* Ajustes — sin implementar */ break;
                case 2: stop();           break; // Salir
            }
        }
    }

    private void updatePlaying(long elapsedTime) {
        // Pausa
        if (menuController.isPausePressed()) {
            state = GameState.PAUSED;
            return;
        }

        Player player = (Player) map.getPlayer();

        if (player.getState() == Creature.STATE_DEAD) {
            map = mapLoader.reloadMap();
            return;
        }

        boolean exitPressed = controller.update(player, elapsedTime);
        if (exitPressed) {
            state = GameState.MAIN_MENU;
            menuSelection = 0;
            return;
        }

        if (controller.isDebugTogglePressed()) debugHitboxes = !debugHitboxes;

        updateCreatures(elapsedTime, player);
    }

    private void updatePaused() {
        if (menuController.isPausePressed() || menuController.isEscapePressed()) {
            state = GameState.PLAYING;
        }
    }

    private void updateGameOver() {
        // Navegación: 2 botones (Volver a jugar, Menú principal)
        if (menuController.isUpPressed())   menuSelection = Math.max(0, menuSelection - 1);
        if (menuController.isDownPressed()) menuSelection = Math.min(1, menuSelection + 1);

        if (menuController.isEnterPressed()) {
            switch (menuSelection) {
                case 0: startGame();      break; // Volver a jugar
                case 1:                          // Menú principal
                    state = GameState.MAIN_MENU;
                    menuSelection = 0;
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Game loop — draw
    // -------------------------------------------------------------------------

    @Override
    public void draw(Graphics2D g) {
        switch (state) {
            case MAIN_MENU:
                drawBackground(g);
                menuRenderer.drawMainMenu(g, screen.getWidth(), screen.getHeight(), menuSelection);
                break;

            case PLAYING:
                drawGame(g);
                break;

            case PAUSED:
                drawGame(g); // el juego se ve detrás
                menuRenderer.drawPause(g, screen.getWidth(), screen.getHeight());
                break;

            case GAME_OVER:
                drawBackground(g);
                menuRenderer.drawGameOver(g, screen.getWidth(), screen.getHeight(), menuSelection);
                break;
        }
    }

    private void drawGame(Graphics2D g) {
        drawer.draw(g, map, screen.getWidth(), screen.getHeight());
        hud.draw(g, (Player) map.getPlayer(), lives, coins, mapLoader.currentMap, debugHitboxes, screen.getWidth());
        if (debugHitboxes) hud.drawHitboxes(g, map, screen.getWidth(), screen.getHeight());
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screen.getWidth(), screen.getHeight());
    }

    // -------------------------------------------------------------------------
    // CollisionSystem.Listener
    // -------------------------------------------------------------------------

    @Override
    public void onCoinCollected() {
        coins++;
        if (coins >= GameConstants.COINS_PER_EXTRA_LIFE) {
            lives++;
            coins = 0;
        }
    }

    @Override
    public void onGoalReached() {
        map = mapLoader.loadNextMap();
    }

    @Override
    public void onPlayerDied() {
        lives--;
        if (lives <= 0) {
            state = GameState.GAME_OVER;
            menuSelection = 0;
        } else {
            map = mapLoader.reloadMap();
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Reinicia y comienza la partida desde el mapa 1. */
    private void startGame() {
        lives = GameConstants.STARTING_LIVES;
        coins = 0;
        mapLoader.currentMap = 0;
        map = mapLoader.loadNextMap();
        state = GameState.PLAYING;
        menuSelection = 0;
    }

    private com.TETOSOFT.assets.AssetManager assets() {
        return mapLoader.getAssets();
    }

    private void updateCreatures(long elapsedTime, Player player) {
        float oldPlayerY = physics.update(player, map, elapsedTime);
        player.update(elapsedTime);

        boolean isFalling = player.getVelocityY() >= 0 && oldPlayerY < player.getY();
        collision.checkPlayerCollisions(player, map, isFalling);

        // Lista de spawns recogidos este frame — se añaden al mapa después
        // del loop para no modificar el iterador mientras itera.
        java.util.List<Sprite> toAdd = new java.util.ArrayList<>();

        Iterator<Sprite> it = map.getSprites();
        while (it.hasNext()) {
            Sprite sprite = it.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature) sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    it.remove();
                    continue;
                }
                physics.update(creature, map, elapsedTime);
            }
            sprite.update(elapsedTime);

            // Recoger spawns de SpawnerGrub
            if (sprite instanceof SpawnerGrub) {
                Sprite spawn = ((SpawnerGrub) sprite).pollSpawn();
                if (spawn != null) toAdd.add(spawn);
            }
        }

        for (Sprite s : toAdd) map.addSprite(s);
    }
}
