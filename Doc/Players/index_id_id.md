# Panduan Pemain — Vanilla Outsider: Better Dogs (Bahasa Indonesia)

Selamat datang di panduan resmi pemain untuk **Vanilla Outsider: Better Dogs**. Mod ini merombak total AI serigala dan anjing jinak di Minecraft dengan kepribadian unik, genetika ukuran dinamis, mode patroli penjaga cerdas, dan sistem interaksi sosial autentik.

---

## 🚀 1. Panduan Memulai Cepat (Quick Start)

1. **Instalasi**:
   - Pasang Fabric Loader yang sesuai dengan versi Minecraft Anda (`1.20.1`, `1.21.1`, `1.21.11`, `26.1.2`, `26.2`, atau `26.3`).
   - Masukkan file JAR `better-dogs-*.jar` dan `dasik-library-*.jar` (untuk versi 26.x+) ke dalam folder `.minecraft/mods`.
2. **Menjinakkan Serigala**:
   - Berikan tulang kepada serigala liar seperti biasa. Saat dijinakkan, anjing Anda akan langsung mendapatkan salah satu dari 3 sifat kepribadian utama: **Normal**, **Agresif (Aggressive)**, atau **Pasifis (Pacifist)**.
3. **Melihat Informasi Anjing**:
   - Jika Anda memasang mod **Jade / Waila**, arahkan kursor ke anjing untuk melihat status kepribadian, ukuran skala, camilan favorit, dan status silsilah perkawinan sedarah (*inbred*).

---

## 🧠 2. Tiga Kepribadian Unik (Personalities)

| Kepribadian | Ciri & Perilaku Utama | Penyesuaian Stat |
| :--- | :--- | :--- |
| **Normal** | Seimbang dan setia. Berpatroli di dekat pos saat berjaga, mengikuti pemilik pada jarak standar 10 blok. | Stat standar vanilla (Peluang kabur saat sekarat: 50%). |
| **Agresif (Aggressive)** | Pemberani dan suka menyerang musuh terlebih dahulu. Menjelajah hingga 20 blok ke depan untuk melindungi pemilik. Kebal terhadap kecemasan badai petir. | **+15% Kecepatan Lari**, **-15% Kerusakan**, **-10 HP** (Peluang kabur saat sekarat: 10%). |
| **Pasifis (Pacifist)** | Lemah lembut dan waspada. Berfungsi sebagai alarm senyap dan pendamping dekat (jarak 6 blok). Menghindari pertarungan langsung. | **+20 HP Bonus**, **-10% Kecepatan Lari**, **+15% Kerusakan Balasan**, **+50% Knockback** (Peluang kabur saat sekarat: 100%). Memberikan efek regenerasi di pos jaga. |

---

## 🍖 3. Fitur Interaksi & Perilaku Cerdas

- **Camilan Favorit & Lari Zoomies**: Setiap anjing memiliki camilan makanan favorit rahasia (berdasarkan UUID). Memberikan camilan favorit akan memulihkan kesehatan penuh dan memicu ledakan lari ceria (*Zoomies*).
- **Elus Anjing (Petting)**: Klik kanan anjing jinak dengan tangan kosong sambil berjongkok (*sneak*) untuk mengelusnya, memunculkan partikel hati dan meningkatkan ikatan afinitas.
- **Kecemasan Badai Petir (Storm Anxiety)**: Anjing pasifis dan normal akan gemetar ketakutan saat badai petir. Elus atau beri makan anjing Anda untuk menenangkannya dan raih *advancement* "Aman dan Tenang".
- **Penghindaran Bahaya (Smart Hazard Safety)**: Anjing akan mendeteksi creeper yang menyala dan berlari cepat menghindar pada kecepatan 1.5x, serta menolak melompat dari tebing curam atau lava.
- **Hadiah Pagi Hari (Morning Gifts)**: Setelah menerima cukup banyak interaksi positif dan kasih sayang, anjing yang tidur di dekat tempat tidur Anda akan membawakan hadiah berguna di pagi hari.
- **Tanduk Kambing (Goat Horn Commands)**: Meniup Tanduk Kambing memungkinkan Anda memanggil dan mengarahkan seluruh kawanan anjing Anda dalam radius 64 blok.

---

## 🧬 4. Genetika & Silsilah (Genetics & Inbreeding)

- **Ukuran Skala Alami**: Serigala dapat muncul dengan variasi skala ukuran tubuh dari **0.70x (Kecil)** hingga **1.45x (Raksasa)**.
- **Pewarisan Genetik**: Mengawinkan dua anjing besar secara bertahap dapat menghasilkan garis keturunan anjing raksasa (*advancement* "Dinasti Puncak").
- **Cacat Perkawinan Sedarah (Inbreeding Runt)**: Mengawinkan induk dengan keturunan langsung berisiko melahirkan anjing kerdil dengan penalti genetik. Cacat ini dapat disembuhkan dengan memberikan **Apel Emas (Golden Apple)**.

---

## ⚙️ 5. Perintah & GameRules

- `/gamerule betterdogs:bd_actionbar_feedback true/false`: Mengaktifkan atau menonaktifkan notifikasi aksi di actionbar.
- `/gamerule betterdogs:bd_friendly_fire_protection true/false`: Melindungi anjing dari tebasan pedang pemiliknya yang tidak sengaja.
- `/gamerule betterdogs:bd_storm_anxiety true/false`: Mengaktifkan atau menonaktifkan ketakutan badai petir.
- `/gamerule betterdogs:bd_cliff_safety true/false`: Mencegah anjing melompat dari tebing tinggi.
