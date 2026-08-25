# Referensi Teknis Pengaturan & GameRules

Better Dogs mengekspos seluruh parameter AI, persentase probabilitas, faktor penskalaan, dan mekanik langsung ke GameRules native Minecraft (dikelompokkan di bawah kategori **"Better Dogs"** di antarmuka) dan konfigurasi visual di sisi klien.

---

## ⚙️ Referensi GameRules Bawaan

Berikut adalah rincian mendalam GameRules yang didukung oleh mod, dikategorikan berdasarkan fungsinya:

### 1. Pengaturan Umum & Bertahan Hidup
| GameRule | Tipe Data | Nilai Bawaan | Deskripsi |
| :--- | :--- | :--- | :--- |
| `bd_fire_survival` | Boolean | `true` | Mengaktifkan lari cepat ke air terdekat dan pelarian darurat saat anjing terbakar. |
| `bd_storm_anxiety` | Boolean | `true` | Mengaktifkan kecemasan badai petir dan pencarian tempat berteduh untuk anjing yang tidak ditenangkan. |
| `bd_scavenge_feeding` | Boolean | `true` | Memungkinkan anjing jinak mencari dan memakan daging yang jatuh di tanah untuk menyembuhkan dirinya. |
| `bd_calm_down` | Boolean | `true` | Memungkinkan menghapus status marah anjing melalui Shift+Klik Kanan dengan tangan kosong. |
| `bd_paper_adoption` | Boolean | `true` | Memungkinkan pemain menempatkan anjing jinak ke dalam status adopsi menggunakan Kertas. |
| `bd_friendly_fire_protection` | Boolean | `true` | Melindungi anjing jinak dari kerusakan tebasan pedang (*sweeping edge*) milik pemiliknya. |
| `bd_morning_gifts` | Boolean | `true` | Mengaktifkan pengiriman hadiah pagi saat bangun tidur atau di awal fajar. |
| `bd_gift_feed_threshold` | Integer | `10` | Jumlah interaksi positif yang dibutuhkan sebelum anjing membawakan hadiah pagi. |
| `bd_gift_interaction_cooldown` | Integer | `100` | Jeda waktu tick antar interaksi positif untuk dihitung ke poin hadiah. |
| `bd_demerit_accidental_attacks` | Boolean | `true` | Jika aktif, serangan tidak sengaja ke anjing akan mereset poin hadiah. |

### 2. Pengaturan Genetika & Pembiakan
| GameRule | Tipe Data | Nilai Bawaan | Deskripsi |
| :--- | :--- | :--- | :--- |
| `bd_inbreeding_penalties` | Boolean | `true` | Mengaktifkan penalti anak kerdil, kecepatan rendah, dan darah rendah saat mengawinkan sesama kerabat dekat. |
| `bd_litter_max_size` | Integer | `4` | Membatasi jumlah maksimal anak anjing yang dapat lahir dalam satu perkawinan. |
| `bd_breeding_same_parent_chance` | Integer | `80` | Persentase probabilitas ($80\%$) bahwa anak mewarisi kepribadian yang sama dari kedua induk yang identik. |
| `bd_breeding_same_parent_other_chance`| Integer | `10` | Persentase probabilitas ($10\%$) untuk masing-masing kepribadian alternatif jika gagal mewarisi kepribadian induk. |

### 3. Pengaturan Sebaran Kawanan & Taktik Berburu
| GameRule | Tipe Data | Nilai Bawaan | Deskripsi |
| :--- | :--- | :--- | :--- |
| `bd_tamed_pack_spread_multiplier` | Double | `0.5` | Faktor pengali jarak yang diterapkan pada kawanan jinak saat bergerak bersama. |
| `bd_tamed_pack_spread_max` | Double | `2.5` | Batas jarak maksimum absolut (dalam blok) antar pengikut anjing jinak. |
| `bd_wild_pack_spread_multiplier` | Double | `0.75` | Faktor pengali jarak yang diterapkan pada kawanan serigala liar saat bergerak bersama. |
| `bd_wild_pack_spread_max` | Double | `3.5` | Batas jarak maksimum absolut (dalam blok) antar pengikut serigala liar. |
| `bd_pack_flanking_tactics` | Boolean | `true` | Mengaktifkan taktik perburuan kawanan (mengepung dan menyerang musuh dari samping/belakang). |

### 4. Pengaturan Mode Penjaga (*Guard Mode*)
| GameRule | Tipe Data | Nilai Bawaan | Deskripsi |
| :--- | :--- | :--- | :--- |
| `bd_guard_range` | Integer | `16` | Jarak jangkauan maksimal (dalam blok) seekor anjing penjaga dapat mengejar musuh dari pos jaganya. |
| `bd_guard_buffs` | Boolean | `true` | Memungkinkan anjing Pasifis penjaga memberikan efek Regenerasi dan Ketahanan kepada pemilik saat alarm berbunyi. |
| `bd_guard_line_of_sight` | Boolean | `true` | Membatasi serangan anjing penjaga hanya pada musuh yang memiliki garis pandang bebas (mencegah aggro tembus dinding). |

### 5. Pengaturan Wilayah Kawanan Liar
| GameRule | Tipe Data | Nilai Bawaan | Deskripsi |
| :--- | :--- | :--- | :--- |
| `bd_wild_pack_dominance_matrix` | Boolean | `true` | Mengaktifkan perhitungan Matriks Probabilitas Sengketa Wilayah saat dua kawanan liar bertemu. |
| `bd_wild_pack_duel_chance` | Integer | `30` | Persentase probabilitas ($30\%$) bahwa sengketa kawanan diselesaikan melalui duel sinematik 1v1 antar pemimpin. |

---

## 🎨 Konfigurasi Visual Sisi Klien (ModMenu & Cloth Config / YACL)

Untuk pengaturan rendering di sisi klien yang tidak memengaruhi logika dunia, opsi dapat dikonfigurasi melalui layar **ModMenu** + **Cloth Config / YACL**:

### 1. `guardParticleDensity`
Menentukan jumlah partikel debu merah berarah yang ditembakkan saat Alarm Pengawas aktif:
* **Tinggi (*High*)**: Memunculkan 12 partikel (jarak $30^\circ$).
* **Sedang (*Medium*, Bawaan)**: Memunculkan 6 partikel (jarak $60^\circ$).
* **Rendah (*Low*)**: Memunculkan 3 partikel (jarak $120^\circ$).
* **Mati (*Off*)**: Menonaktifkan partikel sepenuhnya.

### 2. Jaminan Isolasi Klien
Layar konfigurasi ini dimuat secara *lazy* dan terisolasi khusus di sisi klien. Pada server terdedikasi (*dedicated server*), kelas konfigurasi ini tidak akan dimuat sehingga terhindar dari kerusakan *classloading*.
