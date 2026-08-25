# Selamat Datang di Wiki Better Dogs

🌐 **Bahasa / Languages**: [[🇺🇸 English|Home]] | [[🇮🇩 Bahasa Indonesia|id_id-Home]]

Selamat datang di wiki referensi mendalam untuk **Better Dogs**, sebuah mod perombakan gameplay untuk serigala dan anjing di Minecraft. Wiki ini disusun berdasarkan tonggak sejarah pembaruan (*milestone*) utama yang membentuk mekanik, arsitektur, dan opsi konfigurasi mod ini.

> 📌 **Penafian Kode Sumber Repositori**: Dokumentasi dalam Wiki ini mencerminkan **status kode sumber terbaru di repositori**, yang mungkin mencakup komit terbaru atau fitur yang sedang dikembangkan sebelum rilis publik di CurseForge dan Modrinth.

---

## 🎮 Kompatibilitas Versi Minecraft

Better Dogs dirawat secara aktif di berbagai versi Minecraft dengan paritas fitur yang setara:

| Versi Minecraft | Rilis Mod Target | Kebutuhan Dependensi |
| :--- | :--- | :--- |
| **Minecraft 26.3** | `5.0.76+26.3` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 26.2** | `4.24.74+26.2` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 26.1.2** | `3.10.x+26.1.2` | Java 25+, Fabric API, DasikLibrary |
| **Minecraft 1.21.11** | `2.x+1.21.11` | Java 21, Fabric API |
| **Minecraft 1.21.1** | `1.x+1.21.1` | Java 21, Fabric API |
| **Minecraft 1.20.1** | `1.0.x+1.20.1` | Java 17, Fabric API |

---

## 🗺️ Navigasi Halaman Wiki

Jelajahi halaman wiki berdasarkan tonggak pembaruan fitur utama:

### 1. [[Pembaruan: Kepribadian Utama, Pembiakan & Genetika|id_id-Milestone-Core-Personalities-and-Genetics]]
*Fondasi sistem genetika dan kepribadian.* Mencakup matriks kepribadian (Agresif, Normal, Pasifis), undian DNA berbasis UUID, pewarisan statistik, pelacakan silsilah (anti-inbreeding), anak kerdil, penyembuhan Apel Emas, Tabel Loot Hadiah Pagi, dan Sistem Kelahiran Banyak (*Litter System*).

### 2. [[Pembaruan: Kawanan Liar & Wilayah Kekuasaan|id_id-Milestone-Wild-Packs-and-Territoriality]]
*Menghidupkan ekosistem kawanan serigala liar.* Mencakup jangkar pemimpin permanen, penskalaan jarak pengikut, matriks perselisihan wilayah, duel 1v1 antar pemimpin, dan mekanik penggabungan kawanan (*yield-and-merge*).

### 3. [[Pembaruan: AI Bertahan Hidup Cerdas, Pemadaman Api & Performa|id_id-Milestone-Smart-Survival-and-Performance]]
*Pembaruan mesin untuk kelangsungan hidup anjing dan stabilitas TPS.* Mencakup AI pencarian air terdekat saat terbakar (*fire survival sprint*), pencegahan jatuh dari jurang, penghindaran bahaya api/lava, pelarian dari desisan Creeper, tag data-driven (`#curiosity_blocks`, `#treats`, `#seats`), makan makanan jatuh di tanah, dan memori zero-allocation FastRandom.

### 4. [[Pembaruan: Mode Penjaga & Anjing Sentinel|id_id-Milestone-Guard-Mode-and-Sentinels]]
*Penyempurnaan perilaku anjing penjaga peliharaan.* Mencakup penjaga interaksi 6D, aktivasi jongkok dengan tulang, pola patroli kepribadian, batas sapuan target, sikap siaga waspada, semprotan partikel trigonometri berarah, dan jeda lolongan bulan kelompok.

### 5. [[Referensi Teknis Pengaturan & GameRules|id_id-Config-and-GameRules]]
*Lembar parameter konfigurasi lengkap.* Mencakup tabel komprehensif seluruh 50+ GameRules, konfigurasi bawaan, dan opsi GUI visual ModMenu / Cloth Config.

---

## 🌟 Filosofi Desain: Vanilla Outsider

Better Dogs berpegang teguh pada filosofi **Vanilla Outsider**:
1. **Tingkatkan, Bukan Gantikan**: Membangun di atas konsep entitas vanilla. Serigala tetap menjadi teman setia tetapi memiliki kecerdasan dan otonomi mandiri yang alami.
2. **Hilangkan Kejanggalan AI**: Mencegah kematian konyol vanilla (tercebur ke lava, melompat dari tebing tinggi) tanpa membuat serigala menjadi kebal (*overpowered*).
3. **Kompatibilitas Klien Vanilla Penuh**: 100% opsional di sisi server. Pemain dengan klien Minecraft vanilla dapat bergabung ke server tanpa perlu memasang mod.
