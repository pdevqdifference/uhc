# UHC Plugin (Spigot 1.8.9)

Plugin sencillo de UHC para **Spigot 1.8.x (enfocado a 1.8.9)**.

## Features
- Estados del juego: `LOBBY`, `RUNNING`, `ENDED`
- `/uhc start` inicia la partida (heal + scatter + gracia)
- `/uhc stop` termina la partida
- Gracia configurable (sin PVP)
- Desactiva regeneración natural (UHC clásico)
- Elimina jugadores al morir (modo espectador)
- WorldBorder con shrink opcional

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
- `/uhc start`
- `/uhc stop`
- `/uhc status`
- `/uhc reload`

