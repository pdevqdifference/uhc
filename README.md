# UHC Plugin (Spigot 1.8.9)

Plugin sencillo de UHC para **Spigot 1.8.x (enfocado a 1.8.9)**.

## Quickstart
1. Compila el plugin.
2. Copia el `.jar` a `plugins/`.
3. Reinicia el servidor.
4. Ejecuta `/uhc start`.

## Features
- Estados del juego: `LOBBY`, `RUNNING`, `ENDED`
- `/uhc start` inicia la partida (heal + scatter + gracia)
- `/uhc stop` termina la partida
- Gracia configurable (sin PVP)
- Desactiva regeneración natural (UHC clásico)
- Elimina jugadores al morir (modo espectador)
- WorldBorder con shrink opcional
- Sistema de **scenarios** (CutClean, Timber, HasteyBoys)
- Herramientas de **host** (freeze/unfreeze, revive, invsee, tpall, tpalive, lock/unlock)
- Ajuste rápido de config en runtime con `/uhc config set <path> <valor>`

## Requisitos
- Java 8
- Maven
- Spigot/Paper 1.8.9 (o 1.8.8 compatible)

## Build
```bash
mvn -q -DskipTests package
```

El jar sale en `target/uhc-plugin-1.0.0.jar`.

## Instalación
1. Copia el jar a `plugins/`
2. Reinicia el servidor
3. Configura `plugins/UHC/config.yml` si quieres

## Comandos
Comandos generales:
- `/uhc help`
- `/uhc status`

Comandos admin (`uhc.admin`):
- `/uhc start`
- `/uhc stop`
- `/uhc reload`

### Scenarios
Comandos host (`uhc.host`):
- `/uhc scenario list`
- `/uhc scenario on <nombre>`
- `/uhc scenario off <nombre>`

Scenarios disponibles:
- `cutclean`
- `timber`
- `hasteyboys`

### Host
Comandos host (`uhc.host`):
- `/uhc host freeze <player>`
- `/uhc host unfreeze <player>`
- `/uhc host freezeall`
- `/uhc host unfreezeall`
- `/uhc host revive <player>`
- `/uhc host invsee <player>`
- `/uhc host tpall`
- `/uhc host tpalive`
- `/uhc host lock`
- `/uhc host unlock`

### Config
Comandos admin (`uhc.admin`):
- `/uhc config set <path> <valor>`

Ejemplos:
- `/uhc config set grace-period-seconds 90`
- `/uhc config set world-border.enabled false`
- `/uhc config set scenarios.cutclean true`

## Permisos
- `uhc.admin` (por defecto: `op`)
- `uhc.host` (por defecto: `op`)

## Configuración (config.yml)
Además de las opciones base del juego, puedes activar/desactivar scenarios así:

```yml
scenarios:
  cutclean: false
  timber: false
  hasteyboys: false
```

### Configuración rápida
- Para jugar UHC “clásico”: `disable-natural-regen: true`
- Para desactivar borde: `world-border.enabled: false`
- Para activar un scenario: `scenarios.cutclean: true`

