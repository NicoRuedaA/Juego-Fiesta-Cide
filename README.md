[Leer en Español](ES.md)

# Super Cide Bros

<p align="center">
  <a href="https://www.java.com/">
    <img src="https://img.shields.io/badge/Java-8+-orange?logo=java" alt="Java 8+" />
  </a>
  <img src="https://img.shields.io/badge/Platform-2D%20Platformer-blue" alt="2D Platformer" />
  <img src="https://img.shields.io/badge/Event-Fiesta%20CIDE%202026-green" alt="Fiesta CIDE 2026" />
</p>

<p align="center">
  <img src="/Docs/preview.gif" alt="Gameplay preview" />
</p>

A classic 2D platformer game developed in **Java** for the **Fiesta CIDE 2026** celebration at [CIDE (Centre Internacional d'Educació)](https://www.cide.es).

Collect coins, defeat enemies, and complete 4 challenging levels. Features custom physics, animated sprites, and parallax backgrounds.

---

> **Status:** Released  
> **Last Updated:** 26-APR-2026

---

## 1. What is Super Cide Bros?

A retro-styled 2D platformer where you guide the main character through increasingly challenging levels. Stomp on enemies, gather coins, and reach the goal to progress.

- **4 unique levels** with custom tile-based maps
- **Enemy types:** Grubs (ground crawlers), Flies (flying enemies), and Spawner Grubs
- **Variable jump mechanic** — hold the jump button for higher leaps
- **Sprint system** — move faster by holding Sprint (Shift or Z)
- **Duck mechanic** — crouch to avoid obstacles or plan your next move
- **Parallax scrolling** — 4-layer animated backgrounds
- **HUD system** — lives, coins, and current level display
- **Pause/Resume** — take a break without losing progress
- **Debug mode** — press F1 to toggle hitbox visualization

**Architecture:** Object-Oriented with component-based subsystems (Collision, Physics, Input, Rendering)

---

## 2. Game Screens

| Screen | Description |
|--------|-------------|
| **Main Menu** | Title screen with Play / Settings (coming soon) / Exit |
| **Playing** | Active gameplay with HUD overlay |
| **Pause** | Semi-transparent overlay — press P or ESC to resume |
| **Game Over** | Shows when all lives are lost — Retry or return to menu |
| **Victory** | Congratulations after completing all 4 levels |

---

## 3. Implemented Systems

| System | Status | Description |
|--------|--------|-------------|
| **Player Movement** | ✅ | Walk, run, jump, duck, variable jump height |
| **Physics Engine** | ✅ | Custom gravity, falling acceleration, collision response |
| **Collision Detection** | ✅ | Tile-based collision with entity callbacks |
| **Enemy AI** | ✅ | Ground and flying enemies with patrol behavior |
| **Coin Collection** | ✅ | Coins with extra-life reward every 100 |
| **Level Loading** | ✅ | ASCII-based map files loaded at runtime |
| **Parallax Backgrounds** | ✅ | 4-layer depth scrolling based on player position |
| **HUD** | ✅ | Lives, coins, and level indicator |
| **Pause System** | ✅ | ESC or P to pause/resume |
| **Debug Hitboxes** | ✅ | F1 toggles collision debug overlay |
| **CRT Overlay Effect** | ✅ | Retro scanline visual effect |

---

## 4. Technical Requirements

| Technology | Version | Notes |
|------------|---------|-------|
| Java | **8+** | Standard library only (`java.awt`) |
| IDE | **NetBeans** | Project ready for NetBeans; compiles from CLI too |

### Running the Game

**Option 1 — Double click:**
```
SuperCideBros.jar
```

**Option 2 — Command line:**
```bash
java -jar SuperCideBros.jar
```

**Option 3 — Compile from source:**
```bash
# Compile
javac -d build src/com/TETOSOFT/**/*.java src/com/TETOSOFT/*.java

# Package with resources
jar cfm SuperCideBros.jar manifest.mf -C build com -C images . -C maps .
```

> **Note:** The JAR must be run from the same directory where `images/` and `maps/` are located (or use the included `.jar` which bundles them).

---

## 5. Controls

### Gameplay

| Input | Action | Alternative |
|-------|--------|-------------|
| `←` / `→` | Move left / right | `A` / `D` |
| `↑` | Jump | `W`, `Space` |
| `↓` | Duck / Crouch | — |
| `Shift` | Sprint (hold) | `Z` |
| `P` | Pause / Resume | `ESC` |
| `F1` | Toggle debug hitboxes | — |

### Menu Navigation

| Input | Action |
|-------|--------|
| `↑` / `↓` | Navigate options |
| `Enter` | Select |
| `ESC` | Back / Exit |

---

## 6. Project Structure

```
SuperCideBros/
├── src/com/TETOSOFT/
│   ├── core/
│   │   ├── GameCore.java         # Abstract game loop base class
│   │   └── GameConstants.java     # All tunable constants (physics, speeds, etc.)
│   ├── graphics/
│   │   ├── Animation.java        # Sprite animation system
│   │   ├── CRTOverlay.java       # CRT scanline visual effect
│   │   ├── ScreenManager.java    # Fullscreen display manager
│   │   └── Sprite.java           # Base sprite class
│   ├── input/
│   │   ├── GameAction.java       # Bindable input actions
│   │   └── InputManager.java     # Keyboard/mouse input handling
│   ├── assets/
│   │   ├── AssetManager.java     # Image loader with JAR/dev fallback
│   │   ├── MapParser.java        # ASCII map file parser
│   │   └── SpriteFactory.java    # Sprite prototype factory
│   └── tilegame/
│       ├── GameEngine.java       # Main game logic & state machine
│       ├── MapLoader.java        # Sequential map loading
│       ├── TileMap.java          # Tile map data structure
│       ├── TileMapDrawer.java    # Map renderer with parallax
│       ├── sprites/
│       │   ├── Player.java       # Player with jump/sprint/duck mechanics
│       │   ├── Creature.java     # Base class for all enemies
│       │   ├── Grub.java         # Ground-crawling enemy
│       │   ├── Fly.java          # Flying enemy
│       │   ├── VariantFly.java   # Alternative flying enemy
│       │   ├── SpawnerGrub.java  # Enemy spawner
│       │   ├── Coin.java         # Collectible coin
│       │   ├── PowerUp.java      # Generic power-up base
│       │   └── Goal.java         # Level exit marker
│       └── systems/
│           ├── PlayerController.java  # Player input processing
│           ├── PhysicsSystem.java     # Gravity & movement physics
│           ├── CollisionSystem.java   # Tile/entity collision detection
│           ├── HudRenderer.java       # HUD drawing (lives, coins, level)
│           ├── MenuRenderer.java      # Menu/Game Over/Victory screens
│           └── MenuController.java    # Menu navigation input
│
├── images/                       # Sprites, tiles, backgrounds
├── maps/                         # ASCII level files (map1.txt - map4.txt)
├── Docs/                         # Media & documentation
├── build.xml                     # Ant build configuration
├── manifest.mf                   # JAR manifest
└── SuperCideBros.jar             # Runnable game build
```

---

## 7. Map Format

Levels are stored as `.txt` files in `maps/`. Characters represent tiles and sprites:

| Char | Meaning |
|------|---------|
| `A`-`Z` | Tile images (`A.png`, `B.png`...) |
| `o` | Coin |
| `*` | Goal (level exit) |
| `1` | Grub (ground enemy) |
| `2` | Fly (flying enemy) |
| `3` | Grub Spawner |
| `!` | Music (not yet implemented) |

---

## 8. Code Conventions

### Package Structure

```java
com.TETOSOFT          // Main package
com.TETOSOFT.core     // Game loop & constants
com.TETOSOFT.graphics // Rendering & animation
com.TETOSOFT.input    // Input handling
com.TETOSOFT.assets   // Resource loading
com.TETOSOFT.tilegame // Game logic & systems
```

### Naming Conventions

- **Classes:** PascalCase (e.g., `PlayerController`, `CollisionSystem`)
- **Methods:** camelCase (e.g., `update()`, `checkPlayerCollisions()`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `GRAVITY`, `PLAYER_WALK_SPEED`)
- **Files:** Same as class name + `.java`

### Variable Jump Mechanic

The jump system supports three behaviors controlled by constants in `GameConstants.java`:

```java
PLAYER_JUMP_SPEED          // Initial impulse on jump press
PLAYER_JUMP_HOLD_FORCE     // Extra upward force while holding
PLAYER_JUMP_HOLD_MAX       // Max ms the boost applies (prevents infinite flight)
```

---

## 9. Troubleshooting

### "Game doesn't start"
1. Verify Java 8+ is installed: `java -version`
2. Run from the project root directory (where `images/` and `maps/` are)
3. Try: `java -jar SuperCideBros.jar`

### "Images don't load"
- Ensure `images/` folder is in the same directory as the `.jar`
- Check for `images/A.png`, `images/B.png`, etc.

### "Lag / Low FPS"
- The game uses full-screen mode by default
- Press `F1` to check if debug hitboxes are affecting performance

---

## 10. Changelog

### v1.0 — Fiesta CIDE 2026 (2026-04-26)
- ✅ Feat: Full platformer engine with physics
- ✅ Feat: 4 playable levels
- ✅ Feat: Variable jump, sprint, and duck mechanics
- ✅ Feat: Enemy AI (Grub, Fly, Spawner)
- ✅ Feat: Coin collection with extra-life system
- ✅ Feat: Parallax scrolling backgrounds (4 layers)
- ✅ Feat: CRT visual effect overlay
- ✅ Feat: Pause, Game Over, and Victory screens
- ✅ Feat: Debug hitbox mode (F1)
- ✅ Docs: README created

---

## 11. Roadmap

| Feature | Status |
|---------|--------|
| Core Platformer | ✅ DONE |
| 4 Levels | ✅ DONE |
| Enemies | ✅ DONE |
| HUD System | ✅ DONE |
| Menu Screens | ✅ DONE |
| Sound / Music | 📋 BACKLOG |
| Additional Levels | 📋 BACKLOG |
| Power-Up System | 📋 BACKLOG |
| Settings Menu | 📋 BACKLOG |

---

## 12. Resources

- **Repository:** https://github.com/NicoRuedaA/SuperCideBros
- **Documentation:** `Docs/`
- **CIDE Website:** https://www.cide.es

---

*Developed with Java + passion*

---

