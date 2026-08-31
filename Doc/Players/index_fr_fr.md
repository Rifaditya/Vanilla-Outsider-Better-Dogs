# Guide du Joueur — Vanilla Outsider: Better Dogs (Français)

Bienvenue dans le guide officiel de **Vanilla Outsider: Better Dogs** ! Ce mod repense entièrement l'intelligence artificielle des loups et des chiens apprivoisés dans Minecraft, leur conférant des personnalités expressives, une génétique de taille dynamique, un mode sentinelle intelligent et des interactions sociales riches.

---

## 🚀 1. Guide de Démarrage Rapide (Quick Start)

1. **Installation** :
   - Installez Fabric Loader pour votre version de Minecraft (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2` ou `26.3`).
   - Placez `better-dogs-*.jar` (et `dasik-library-*.jar` pour la version 26.x+) dans votre dossier `.minecraft/mods`.
2. **Apprivoiser un Loup** :
   - Donnez des os à un loup sauvage comme dans le jeu de base. Dès son apprivoisement, le chien recevra l'une des trois personnalités uniques : **Normal**, **Agressif** ou **Pacifiste**.
3. **Afficher les Attributs du Chien** :
   - Avec le mod **Jade / Waila** installé, regardez votre chien pour voir sa personnalité, son échelle de taille, sa friandise secrète préférée et son statut de consanguinité.

---

## 🧠 2. Les Trois Types de Personnalités (Personalities)

| Personnalité | Comportement Principal | Ajustements d'Attributs |
| :--- | :--- | :--- |
| **Normal** | Compagnon équilibré et fidèle. Patrouille près du poste de garde et suit son maître à la distance standard de 10 blocs. | Attributs vanilla standards (chance de fuite à basse vie : 50%). |
| **Agressif (Aggressive)** | Protecteur intrépide. Détecte et attaque automatiquement les monstres hostiles jusqu'à 20 blocs et poursuit jusqu'à 50 blocs. Insensible à la peur des orages. | **+15% vitesse de sprint**, **-15% dégâts**, **-10 PV max** (chance de fuite : 10%). |
| **Pacifiste (Pacifist)** | Sentinelle attentive et alarme vivante (reste à moins de 6 blocs du maître). Évite le combat direct. | **+20 PV max**, **-10% vitesse**, **+15% dégâts de riposte**, **+50% recul** (chance de fuite : 100%). Confère une aura de régénération et résistance en mode garde. |

---

## 🍖 3. Interactions et Survie Intelligente

- **Friandise Secrète & Sprint Fou (Zoomies)** : Chaque chien a une friandise secrète préférée liée à son UUID. La lui donner régénère totalement sa santé et déclenche un joyeux sprint fou (*Zoomies*).
- **Caresses à Main Nue (Petting)** : Accroupissez-vous et faites un clic droit avec la main vide sur votre chien pour le caresser, faire apparaître des cœurs et renforcer votre lien.
- **Anxiété des Orages (Storm Anxiety)** : Pendant les orages, les chiens normaux et pacifistes tremblent et gémissent de peur. Caressez-les ou nourrissez-les pour les apaiser (progrès « Sain et Sauf »).
- **Esquive Intelligente des Dangers (Smart Hazard Safety)** : Les chiens détectent les creepers amorcés et s'enfuient à 1.5x de vitesse ; ils évitent également de sauter des falaises dangereuses ou dans la lave.
- **Cadeaux du Matin (Morning Gifts)** : En prenant soin continuellement de votre chien, s'il dort près de votre lit, il vous offrira des cadeaux utiles au réveil.
- **Ordres à la Corne de Chèvre (Goat Horn Commands)** : Utilisez une corne de chèvre pour appeler et coordonner toute votre meute dans un rayon de 64 blocs.

---

## 🧬 4. Génétique & Consanguinité (Genetics & Inbreeding)

- **Variation Naturelle de Taille** : Les loups sauvages apparaissent avec des échelles comprises entre **0.70x (petit)** et **1.45x (géant)**.
- **Héritage Génétique** : Accoupler des chiens de grande taille permet de créer une véritable lignée géante (progrès « Dynastie des Géants »).
- **Chiots Chétifs Consanguins (Inbreeding Runt)** : Croiser des proches parents peut donner naissance à un chiot chétif affaibli. Donnez-lui une **Pomme Dorée (Golden Apple)** pour soigner définitivement ce défaut génétique.

---

## ⚙️ 5. Règles de Jeu Utiles (GameRules)

- `/gamerule betterdogs:bd_actionbar_feedback true/false` : Active/désactive les notifications dans la barre d'action.
- `/gamerule betterdogs:bd_friendly_fire_protection true/false` : Active la protection contre les coups accidentels du maître.
- `/gamerule betterdogs:bd_storm_anxiety true/false` : Active/désactive l'anxiété pendant les orages.
- `/gamerule betterdogs:bd_cliff_safety true/false` : Active/désactive la prévention des chutes de falaises.
