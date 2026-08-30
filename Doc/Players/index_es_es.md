# Guía para Jugadores — Vanilla Outsider: Better Dogs (Mejores Perros - Español)

¡Bienvenido a la guía oficial de **Vanilla Outsider: Better Dogs**! Este mod renueva por completo la inteligencia artificial de los lobos y perros domesticados en Minecraft, otorgándoles personalidades únicas, genética dinámica de tamaños, un modo inteligente de guardia centinela y ricas interacciones sociales.

---

## 🚀 1. Guía de Inicio Rápido (Quick Start)

1. **Instalación**:
   - Instala Fabric Loader para tu versión correspondiente de Minecraft (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2` o `26.3`).
   - Coloca `better-dogs-*.jar` (y `dasik-library-*.jar` para versiones 26.x+) en tu carpeta `.minecraft/mods`.
2. **Domesticación de Lobos**:
   - Alimenta a un lobo salvaje con huesos como en el juego base. Al ser domesticado, adquirirá inmediatamente una de las tres personalidades principales: **Normal**, **Agresivo** o **Pacifista**.
3. **Ver Información del Perro**:
   - Si tienes instalado **Jade / Waila**, apunta tu retícula a tu perro para ver su personalidad, escala de tamaño, golosina favorita secreta y estado de endogamia.

---

## 🧠 2. Tres Personalidades Únicas (Personalities)

| Personalidad | Comportamiento Principal | Ajustes de Estadísticas |
| :--- | :--- | :--- |
| **Normal** | Compañero equilibrado y leal. Patrulla cerca de su puesto de guardia y sigue al dueño a una distancia estándar de 10 bloques. | Estadísticas estándar de vainilla (probabilidad de huida con salud baja: 50%). |
| **Agresivo (Aggressive)** | Protector valiente. Detecta y ataca automáticamente monstruos hostiles a 20 bloques y los persigue hasta 50 bloques. Inmune al miedo por tormentas. | **+15% de velocidad de carrera**, **-15% de daño de ataque**, **-10 HP** (probabilidad de huida: 10%). |
| **Pacifista (Pacifist)** | Centinela atento y alarma silenciosa (permanece a menos de 6 bloques). Evita el combate directo. | **+20 HP de salud máxima**, **-10% de velocidad**, **+15% de daño de contraataque**, **+50% de empuje** (probabilidad de huida: 100%). Otorga aura de regeneración y resistencia al estar de guardia. |

---

## 🍖 3. Interacciones y Supervivencia Inteligente

- **Golosina Favorita Secreta y Carreras Alegres (Zoomies)**: Cada perro tiene una golosina favorita secreta determinada por su UUID. Alimentarlo con ella restaurará toda su salud y activará un alegre ataque de carreras (*Zoomies*).
- **Caricias con la Mano Vacía (Petting)**: Agáchate y haz clic derecho con la mano vacía sobre tu perro para acariciarlo, emitir partículas de corazones y fortalecer su vínculo afectivo.
- **Ansiedad por Tormentas (Storm Anxiety)**: Durante las tormentas eléctricas, los perros normales y pacifistas tiemblan y gimen de miedo. Acarícialos o aliméntalos para calmarlos y desbloquear el progreso «Sano y Salvo».
- **Evasión Inteligente de Peligros (Smart Hazard Safety)**: Los perros detectan creepers a punto de explotar y huyen corriendo a 1.5x de velocidad; también evitan saltar hacia acantilados peligrosos o lava.
- **Regalos Matutinos (Morning Gifts)**: Tras recibir suficiente cuidado y cariño, un perro que duerma junto a tu cama te traerá útiles regalos a la mañana siguiente.
- **Órdenes con Cuerno de Cabra (Goat Horn Commands)**: Haz sonar un cuerno de cabra para llamar y coordinar a toda tu manada en un radio de 64 bloques.

---

## 🧬 4. Genética y Endogamia (Genetics & Inbreeding)

- **Variación Natural de Tamaño**: Los lobos salvajes aparecen con escalas de **0.70x (pequeño)** hasta **1.45x (gigante)**.
- **Herencia Genética**: Cruzar perros grandes permite establecer una dinastía gigante (progreso «Dinastía Suprema»).
- **Defecto de Endogamia (Inbreeding Runt)**: La reproducción entre parientes cercanos puede producir cachorros enanos con penalizaciones. Aliméntalos con una **Manzana Dorada (Golden Apple)** para curar permanentemente el defecto.

---

## ⚙️ 5. Reglas de Juego Útiles (GameRules)

- `/gamerule betterdogs:bd_actionbar_feedback true/false`: Activa o desactiva notificaciones en la barra de acción.
- `/gamerule betterdogs:bd_friendly_fire_protection true/false`: Activa la protección contra fuego amigo para no dañar a tus perros sin agacharte.
- `/gamerule betterdogs:bd_storm_anxiety true/false`: Activa o desactiva la ansiedad por tormentas.
- `/gamerule betterdogs:bd_cliff_safety true/false`: Activa o desactiva la protección contra caídas en acantilados.
