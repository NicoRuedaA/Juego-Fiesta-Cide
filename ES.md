[Read in English](README.md)

# Super Cide Bros

<p align="center">
  <a href="https://www.java.com/">
    <img src="https://img.shields.io/badge/Java-8+-orange?logo=java" alt="Java 8+" />
  </a>
  <img src="https://img.shields.io/badge/Plataforma-2D%20Platformer-blue" alt="2D Platformer" />
  <img src="https://img.shields.io/badge/Evento-Fiesta%20CIDE%202026-green" alt="Fiesta CIDE 2026" />
</p>

<p align="center">
  <img src="/Docs/preview.gif" alt="Vista previa del juego" />
</p>

Un juego de plataformas 2D clásico desarrollado en **Java** para la celebración de la **Fiesta CIDE 2026** en [CIDE (Centre Internacional d'Educació)](https://www.cide.es).

Recoge monedas, elimina enemigos y completa 4 niveles desafiantes. Incluye físicas personalizadas, sprites animados y fondos con efecto parallax.

---

> **Estado:** Lanzado  
> **Última actualización:** 26-ABR-2026

---

## 1. ¿Qué es Super Cide Bros?

Un plataformas 2D con estética retro donde guías al personaje principal por niveles cada vez más difíciles. Pisa enemigos, recoge monedas y llega a la meta para avanzar.

- **4 niveles únicos** con mapas personalizados basados en tiles
- **Tipos de enemigos:** Grubs (enemigos terrestres), Moscas (enemigos voladores) y Creadores de Grubs
- **Salto variable** — mantén pulsado el botón de salto para saltos más altos
- **Sistema de sprint** — muévete más rápido manteniendo Sprint (Shift o Z)
- **Mecánica de agacharse** — pístate para evitar obstáculos o planificar tu siguiente movimiento
- **Scroll parallax** — 4 capas de fondos animados
- **Sistema HUD** — vidas, monedas y nivel actual
- **Pausa/Reanudar** — toma un descanso sin perder progreso
- **Modo debug** — presiona F1 para visualizar hitboxes
- **Efecto CRT** — efecto visual retro de líneas de escaneo

**Arquitectura:** Orientada a objetos con sistemas basados en componentes (Colisión, Física, Input, Renderizado)

---

## 2. Pantallas del Juego

| Pantalla | Descripción |
|----------|-------------|
| **Menú Principal** | Pantalla de título con Jugar / Ajustes (próximamente) / Salir |
| **Jugando** | Gameplay activo con overlay del HUD |
| **Pausa** | Overlay semitransparente sobre el juego — pulsa P o ESC para continuar |
| **Game Over** | Se muestra al perder todas las vidas — Reintentar o volver al menú |
| **Victoria** | Felicidades tras completar los 4 niveles |

---

## 3. Sistemas Implementados

| Sistema | Estado | Descripción |
|---------|--------|-------------|
| **Movimiento del jugador** | ✅ | Caminar, correr, saltar, agacharse, altura de salto variable |
| **Motor de físicas** | ✅ | Gravedad personalizada, aceleración en caída, respuesta de colisión |
| **Detección de colisiones** | ✅ | Colisión basada en tiles con callbacks de entidades |
| **IA de enemigos** | ✅ | Enemigos terrestres y voladores con comportamiento de patrulla |
| **Recolección de monedas** | ✅ | Monedas con recompensa de vida extra cada 100 |
| **Carga de niveles** | ✅ | Archivos de mapa en formato ASCII cargados en tiempo de ejecución |
| **Fondos parallax** | ✅ | 4 capas de profundidad con scroll basado en posición del jugador |
| **HUD** | ✅ | Vidas, monedas e indicador de nivel |
| **Sistema de pausa** | ✅ | ESC o P para pausar/reanudar |
| **Hitboxes de debug** | ✅ | F1 activa overlay de debug de colisiones |
| **Efecto CRT Overlay** | ✅ | Efecto visual de líneas de escaneo retro |

---

## 4. Requisitos Técnicos

| Tecnología | Versión | Notas |
|------------|---------|-------|
| Java | **8+** | Solo biblioteca estándar (`java.awt`) |
| IDE | **NetBeans** | Proyecto listo para NetBeans; también compila desde CLI |

### Ejecutar el Juego

**Opción 1 — Doble clic:**
```
SuperCideBros.jar
```

**Opción 2 — Línea de comandos:**
```bash
java -jar SuperCideBros.jar
```

**Opción 3 — Compilar desde código fuente:**
```bash
# Compilar
javac -d build src/com/TETOSOFT/**/*.java src/com/TETOSOFT/*.java

# Empaquetar con recursos
jar cfm SuperCideBros.jar manifest.mf -C build com -C images . -C maps .
```

> **Nota:** El JAR debe ejecutarse desde el mismo directorio donde estén `images/` y `maps/` (o usa el `.jar` incluido que los incluye).

---

## 5. Controles

### Durante el Juego

| Tecla | Acción | Alternativa |
|-------|--------|-------------|
| `←` / `→` | Mover izquierda / derecha | `A` / `D` |
| `↑` | Saltar | `W`, `Espacio` |
| `↓` | Agacharse | — |
| `Shift` | Sprint (mantener) | `Z` |
| `P` | Pausar / Reanudar | `ESC` |
| `F1` | Alternar hitboxes de debug | — |

### Navegación en Menús

| Tecla | Acción |
|-------|--------|
| `↑` / `↓` | Navegar opciones |
| `Enter` | Seleccionar |
| `ESC` | Volver / Salir |

---

## 6. Estructura del Proyecto

```
SuperCideBros/
├── src/com/TETOSOFT/
│   ├── core/
│   │   ├── GameCore.java         # Clase base abstracta del bucle de juego
│   │   └── GameConstants.java     # Todas las constantes ajustables (física, velocidades, etc.)
│   ├── graphics/
│   │   ├── Animation.java        # Sistema de animación de sprites
│   │   ├── CRTOverlay.java       # Efecto visual de líneas CRT
│   │   ├── ScreenManager.java    # Gestor de pantalla completa
│   │   └── Sprite.java           # Clase base de sprite
│   ├── input/
│   │   ├── GameAction.java       # Acciones de input vinculables
│   │   └── InputManager.java     # Manejo de teclado/ratón
│   ├── assets/
│   │   ├── AssetManager.java     # Cargador de imágenes con fallback JAR/dev
│   │   ├── MapParser.java        # Parser de archivos de mapa ASCII
│   │   └── SpriteFactory.java    # Fábrica de prototipos de sprites
│   └── tilegame/
│       ├── GameEngine.java       # Lógica principal del juego y máquina de estados
│       ├── MapLoader.java        # Carga secuencial de mapas
│       ├── TileMap.java          # Estructura de datos del mapa de tiles
│       ├── TileMapDrawer.java    # Renderizado del mapa con parallax
│       ├── sprites/
│       │   ├── Player.java       # Jugador con mecánicas de salto/sprint/duck
│       │   ├── Creature.java     # Clase base para todos los enemigos
│       │   ├── Grub.java         # Enemigo terrestre
│       │   ├── Fly.java          # Enemigo volador
│       │   ├── VariantFly.java   # Enemigo volador alternativo
│       │   ├── SpawnerGrub.java  # Creador de enemigos
│       │   ├── Coin.java         # Moneda coleccionable
│       │   ├── PowerUp.java      # Base genérica de power-up
│       │   └── Goal.java         # Marcador de salida de nivel
│       └── systems/
│           ├── PlayerController.java  # Procesamiento de input del jugador
│           ├── PhysicsSystem.java     # Física de gravedad y movimiento
│           ├── CollisionSystem.java   # Detección de colisiones tiles/entidades
│           ├── HudRenderer.java       # Dibujo del HUD (vidas, monedas, nivel)
│           ├── MenuRenderer.java      # Pantallas de menú/Game Over/Victoria
│           └── MenuController.java   # Input de navegación en menús
│
├── images/                       # Sprites, tiles, fondos
├── maps/                         # Archivos de nivel ASCII (map1.txt - map4.txt)
├── Docs/                         # Medios y documentación
├── build.xml                     # Configuración de build Ant
├── manifest.mf                   # Manifiesto del JAR
└── SuperCideBros.jar             # Build ejecutable del juego
```

---

## 7. Formato de Mapas

Los niveles se almacenan como archivos `.txt` en `maps/`. Los caracteres representan tiles y sprites:

| Car. | Significado |
|------|-------------|
| `A`-`Z` | Imágenes de tiles (`A.png`, `B.png`...) |
| `o` | Moneda |
| `*` | Meta (salida del nivel) |
| `1` | Grub (enemigo terrestre) |
| `2` | Mosca (enemigo volador) |
| `3` | Creador de Grubs |
| `!` | Música (aún no implementado) |

---

## 8. Convenciones de Código

### Estructura de Paquetes

```java
com.TETOSOFT          // Paquete principal
com.TETOSOFT.core     // Bucle de juego y constantes
com.TETOSOFT.graphics // Renderizado y animación
com.TETOSOFT.input    // Manejo de input
com.TETOSOFT.assets   // Carga de recursos
com.TETOSOFT.tilegame // Lógica del juego y sistemas
```

### Convenciones de Nombres

- **Clases:** PascalCase (ej., `PlayerController`, `CollisionSystem`)
- **Métodos:** camelCase (ej., `update()`, `checkPlayerCollisions()`)
- **Constantes:** UPPER_SNAKE_CASE (ej., `GRAVITY`, `PLAYER_WALK_SPEED`)
- **Archivos:** Igual que la clase + `.java`

### Mecánica de Salto Variable

El sistema de salto soporta tres comportamientos controlados por constantes en `GameConstants.java`:

```java
PLAYER_JUMP_SPEED          // Impulso inicial al pulsar salto
PLAYER_JUMP_HOLD_FORCE     // Fuerza adicional hacia arriba mientras se mantiene
PLAYER_JUMP_HOLD_MAX       // Ms máximos que aplica el boost (previene vuelo infinito)
```

---

## 9. Solución de Problemas

### "El juego no inicia"
1. Verifica que Java 8+ esté instalado: `java -version`
2. Ejecuta desde la raíz del proyecto (donde estén `images/` y `maps/`)
3. Prueba: `java -jar SuperCideBros.jar`

### "Las imágenes no cargan"
- Asegúrate de que la carpeta `images/` esté en el mismo directorio que el `.jar`
- Verifica que existan `images/A.png`, `images/B.png`, etc.

### "Lag / Baja tasa de FPS"
- El juego usa modo pantalla completa por defecto
- Presiona `F1` para verificar si las hitboxes de debug afectan el rendimiento

---

## 10. Registro de Cambios

### v1.0 — Fiesta CIDE 2026 (2026-04-26)
- ✅ Feat: Motor de plataformas completo con físicas
- ✅ Feat: 4 niveles jugables
- ✅ Feat: Mecánicas de salto variable, sprint y agacharse
- ✅ Feat: IA de enemigos (Grub, Mosca, Creador)
- ✅ Feat: Sistema de recolección de monedas con vida extra
- ✅ Feat: Fondos parallax de 4 capas
- ✅ Feat: Efecto visual CRT overlay
- ✅ Feat: Pantallas de Pausa, Game Over y Victoria
- ✅ Feat: Modo debug de hitboxes (F1)
- ✅ Docs: README creado

---

## 11. Roadmap

| Funcionalidad | Estado |
|--------------|--------|
| Motor de plataformas core | ✅ HECHO |
| 4 Niveles | ✅ HECHO |
| Enemigos | ✅ HECHO |
| Sistema HUD | ✅ HECHO |
| Pantallas de menú | ✅ HECHO |
| Sonido / Música | 📋 BACKLOG |
| Niveles adicionales | 📋 BACKLOG |
| Sistema de Power-Ups | 📋 BACKLOG |
| Menú de Ajustes | 📋 BACKLOG |

---

## 12. Recursos

- **Repositorio:** https://github.com/NicoRuedaA/SuperCideBros
- **Documentación:** `Docs/`
- **Web de CIDE:** https://www.cide.es

---

*Desarrollado con Java + pasión para la Fiesta CIDE 2026*

---

[🇬🇧 Read in English](README.md)
