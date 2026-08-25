# Pembaruan: Kepribadian Utama, Pembiakan & Genetika

Diperkenalkan pada **`v4.0.0` (MC 26.2)** dan diterapkan di seluruh versi yang didukung, pembaruan ini merombak serigala jinak dengan menambahkan kepribadian permanen, undian DNA genetik, pewarisan statistik, pemeriksaan silsilah kekerabatan (*inbreeding*), anak kerdil, penskalaan ukuran tubuh, dan pencarian hadiah pagi berbasis data.

---

## 🧠 Matriks Kepribadian Serigala

Saat dijinakkan, setiap serigala secara permanen memperoleh tipe kepribadian tertentu. Kepribadian memengaruhi atribut pertarungan dasar dan pola patroli dalam **Mode Penjaga**:

| Kepribadian | Bias Atribut | Partikel Visual | Pola Patroli | Peran Utama |
| :--- | :--- | :--- | :--- | :--- |
| **Agresif** | +Kecepatan, +Serangan, -Darah | Merah (`0xFF3333`) | Sapuan perimeter luar (radius 80%) | Pengintai Tempur |
| **Normal** | Seimbang (*Vanilla-plus*) | Emas (`0xFFD700`) | Pos penjaga atau patroli bintang radial | Serbaguna Klasik |
| **Pasifis** | +Darah, +Ketahanan, -Serangan | Toska/Hijau (`0x00FF88`) | Orbit lingkaran pelindung dekat pos | Alarm Pengawas / Penyembuh |

---

## 🧬 Identitas DNA & Undian UUID Unik

Setiap serigala memiliki identitas genetik unik yang ditentukan oleh UUID-nya. Sifat kepribadian, variasi suara lolongan, dan perilaku sosial diproses menggunakan rumus operasi bitwise XOR:

\[\text{Seed} = \text{UUID.getMostSignificantBits()} \oplus \text{UUID.getLeastSignificantBits()} \oplus 7381940\text{L}\]

Benih (*seed*) ini diteruskan ke generator angka acak untuk memastikan kepribadian dan statistik serigala bersifat konsisten, permanen, dan unik.

---

## 📏 Penskalaan Ukuran Tubuh Dinamis

Ukuran fisik serigala di dunia permainan menyesuaikan secara dinamis berdasarkan bonus darah maksimalnya. Serigala yang sehat dan tangguh akan tumbuh lebih besar:

\[\text{Skala} = 1.0 + (\text{healthBonus} \times 0.012)\]

* **Batas Penskalaan Ukuran**:
  * **Anak Kerdil Agresif (Kasus Terkecil)**: $0.808\times$ ukuran normal.
  * **Juara Pasifis (Kasus Terbesar)**: $1.312\times$ ukuran normal.

---

## 🧪 Pewarisan Sifat & Mesin Silsilah Kekerabatan

### 1. Pewarisan Statistik
Saat membiakkan dua serigala, anak serigala mewarisi perpaduan statistik kedua induknya (Darah Maksimal, Kerusakan Serangan, Kecepatan) dengan offset mutasi segitiga:

\[\text{StatistikAnak} = \frac{\text{StatInduk1} + \text{StatInduk2}}{2} + \text{MutasiSegitiga}\]

### 2. Matriks Pewarisan Kepribadian
* **Induk dengan Kepribadian Sama**: Peluang $80\%$ mewarisi kepribadian yang sama; sisa $20\%$ terbagi rata untuk dua kepribadian lainnya.
* **Induk dengan Kepribadian Berbeda**: Peluang $40\%$ untuk Induk 1, $40\%$ untuk Induk 2, dan $20\%$ untuk kepribadian ketiga.

### 3. Pemeriksaan Perkawinan Sedarah (*Inbreeding*)
Sistem melacak garis keturunan untuk mencegah perkawinan sedarah:
* **Tag Data**: `parent1Uuid` dan `parent2Uuid`.
* **Pengecekan**: Jika kedua induk memiliki orang tua yang sama (bersaudara) atau salah satu induk adalah leluhur langsungnya, anak yang lahir akan mengalami status **Anak Kerdil Sedarah (*Inbred Runt*)**.

### 4. Penalti Anak Kerdil Sedarah
* **Penalti Statistik**: Darah maksimal berkurang $50\%$, kecepatan berkurang $30\%$, dan serangan terkunci di batas minimum.
* **Ukuran**: Terkunci pada skala terkecil $0.808\times$.
* **Penyembuhan**: Cacat genetik ini dapat disembuhkan dengan memberinya **Apel Emas (*Golden Apple*)**.

---

## 🐕 Sistem Kelahiran Banyak (*Litter System*)

Membiakkan serigala kini tidak selalu menghasilkan 1 anak saja. Sistem kelahiran mengacak kurva probabilitas sehingga serigala dapat melahirkan beberapa anak sekaligus dalam satu perkawinan:
* Setiap anak serigala dalam kelompok kelahiran mengundi kepribadian, statistik, dan ukurannya secara **independen**.
* Ukuran maksimal kelahiran dapat diatur melalui GameRule `bd_litter_max_size`.

---

## 🎁 Hadiah Pagi & Tabel Loot Berbasis Data

Serigala jinak yang memiliki ikatan kuat dengan pemiliknya dapat membawakan hadiah di pagi hari:
1. **Syarat Pemicu**: Terjadi saat pemilik bangun dari tempat tidur atau secara alami di awal pagi ($0 \le t \le 2000$ tick).
2. **Kualifikasi Mutlak**:
   * Serigala harus dalam kondisi **100% darah penuh** (`wolf.getHealth() >= wolf.getMaxHealth()`).
   * Tidak ada monster jahat dalam radius 16 blok.
   * Serigala telah mengumpulkan poin kebaikan yang cukup (diberi makan, disuruh duduk, dielus).
   * Batas: maksimal 1 kali hadiah per hari permainan.
3. **Tabel Loot Kepribadian**:
   Hadiah diambil langsung dari tabel loot datapack di `data/vanilla-outsider-better-dogs/loot_table/morning_gift/`:
   * `aggressive.json`: Tulang, daging busuk, drop monster, panah.
   * `pacifist.json`: Bunga, sweet berry, apel, biji-bijian.
   * `normal.json`: Ranting/stick, kaki kelinci, kulit, bulu.
   * `rare_treasure.json`: Peluang 5% untuk buku sihir, label nama (*name tag*), apel emas, atau piringan musik.

---

## 🍖 Makanan Favorit & Rasa Penasaran Anak Serigala

* **Makanan Favorit (*Favorite Treats*)**: Setiap serigala menghitung camilan favorit unik dari daftar `#vanilla-outsider-better-dogs:treats` berdasarkan UUID-nya. Memberi makanan favorit akan memicu partikel hati ganda serta efek kecepatan dan regenerasi sementara.
* **Rasa Penasaran Anak Serigala**: Anak serigala yang masih kecil akan secara alami berjalan mendekati tanaman, bunga, dan dedaunan yang terdaftar dalam `#vanilla-outsider-better-dogs:curiosity_blocks` untuk mengendus dengan gembira.
