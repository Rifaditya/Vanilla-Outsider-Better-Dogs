# Spielerhandbuch — Vanilla Outsider: Better Dogs (Bessere Hunde - Deutsch)

Willkommen zum offiziellen Spielerhandbuch für **Vanilla Outsider: Better Dogs**. Diese Mod überarbeitet die künstliche Intelligenz von Wölfen und gezähmten Hunden in Minecraft grundlegend und verleiht ihnen ausdrucksstarke Persönlichkeiten, dynamische Größengenetik, einen intelligenten Wachposten-Modus und lebendige soziale Interaktionen.

---

## 🚀 1. Schnellstartanleitung (Quick Start)

1. **Installation**:
   - Installiere den Fabric Loader für deine Minecraft-Version (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2` oder `26.3`).
   - Lege `better-dogs-*.jar` (und `dasik-library-*.jar` für Versionen ab 26.x) in deinen Ordner `.minecraft/mods`.
2. **Einen Wolf zähmen**:
   - Füttere einen wilden Wolf wie gewohnt mit Knochen. Direkt nach der Zähmung erhält der Hund eine von drei einzigartigen Persönlichkeiten: **Normal**, **Aggressiv** oder **Pazifist**.
3. **Hundeeigenschaften einsehen**:
   - Falls du die Mod **Jade / Waila** installiert hast, siehst du beim Anvisieren deines Hundes seine Persönlichkeit, Größenskalierung, das geheime Lieblingsleckerli und den Inzuchtstatus.

---

## 🧠 2. Die drei Charaktertypen (Personalities)

| Persönlichkeit | Hauptverhaltensmerkmale | Eigenschaftsanpassungen |
| :--- | :--- | :--- |
| **Normal** | Ausgeglichener und treuer Begleiter. Patrouilliert nah am Wachposten und folgt dem Besitzer im Standardabstand von 10 Blöcken. | Standard-Vanilla-Werte (Fluchtchance bei niedriger Gesundheit: 50%). |
| **Aggressiv (Aggressive)** | Furchtloser Beschützer. Erfasst feindliche Monster automatisch im Umkreis von 20 Blöcken und verfolgt sie bis zu 50 Blöcke weit. Keine Gewitterangst. | **+15% Sprinttempo**, **-15% Angriffsschaden**, **-10 HP** (Fluchtchance: 10%). |
| **Pazifist (Pacifist)** | Vorsichtiger Wächter und Frühwarnsystem (weicht kaum von der Seite, max. 6 Blöcke). Meidet direkte Kämpfe. | **+20 HP Maximalgesundheit**, **-10% Tempo**, **+15% Konterschaden**, **+50% Rückstoß** (Fluchtchance: 100%). Verleiht im Wachmodus eine Regenerationsaura. |

---

## 🍖 3. Interaktive Mechaniken & Überleben

- **Geheimes Lieblingsleckerli & Rennanfälle (Zoomies)**: Jeder Hund besitzt basierend auf seiner UUID ein individuelles Lieblingsleckerli. Das Füttern heilt ihn vollständig und löst ausgelassene Rennanfälle (*Zoomies*) aus.
- **Streicheln mit leerer Hand (Petting)**: Schleiche und klicke mit leerer Hand mit Rechts auf deinen Hund, um ihn zu streicheln, Herzchen aufsteigen zu lassen und die Zuneigung zu steigern.
- **Gewitterangst & Beruhigung (Storm Anxiety)**: Bei Gewitter zittern und winseln normale und pazifistische Hunde vor Angst. Streichle oder füttere sie, um sie zu beruhigen (Fortschritt «In Sicherheit und Geborgenheit»).
- **Intelligente Gefahrenvermeidung (Smart Hazard Safety)**: Hunde erkennen zündende Creeper und sprinten mit 1.5x Tempo davon; gefährliche Klippen und Lava werden automatisch gemieden.
- **Morgengeschenke (Morning Gifts)**: Nach ausreichend Pflege und Zuneigung bringt dir ein Hund, der neben deinem Bett geschlafen hat, am Morgen nützliche Geschenke.
- **Ziegenhorn-Befehle (Goat Horn Commands)**: Blase in ein Ziegenhorn, um dein gesamtes Rudel im Umkreis von 64 Blöcken herbeizurufen und zu koordinieren.

---

## 🧬 4. Genetik & Inzucht (Genetics & Inbreeding)

- **Größenvarianz**: Wilde Wölfe spawnen in Größen von **0.70x (Kompakt/Klein)** bis **1.45x (Riesig)**.
- **Vererbung**: Durch gezielte Zucht großer Hunde lässt sich eine Riesenlinie aufbauen (Fortschritt «Riesen-Dynastie»).
- **Inzucht-Kümmerlinge (Inbreeding Runt)**: Bei Verwandtenpaarungen können geschwächte Kümmerlinge entstehen. Füttere einen Kümmerling mit einem **Goldenen Apfel (Golden Apple)**, um den genetischen Defekt dauerhaft zu heilen.

---

## ⚙️ 5. Wichtige Spieleinstellungen (GameRules)

- `/gamerule betterdogs:bd_actionbar_feedback true/false`: Aktionsleisten-Benachrichtigungen an/aus.
- `/gamerule betterdogs:bd_friendly_fire_protection true/false`: Schutz vor versehentlichem Schlagen eigener Hunde aktivieren.
- `/gamerule betterdogs:bd_storm_anxiety true/false`: Gewitterangst an/aus.
- `/gamerule betterdogs:bd_cliff_safety true/false`: Klippensicherheit an/aus.
