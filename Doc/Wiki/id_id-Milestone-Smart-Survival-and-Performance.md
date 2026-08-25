# Pembaruan: AI Bertahan Hidup Cerdas, Pemadaman Api & Performa

Diperkenalkan pada **`v4.7.0` (MC 26.2)** dan disempurnakan di seluruh versi modern, pembaruan ini meningkatkan ketahanan serigala dari bahaya lingkungan, menghadirkan AI pencari air saat terbakar, integrasi tag datapack, dan arsitektur memori berkinerja tinggi tanpa alokasi (*zero-allocation*).

---

## 🛡️ AI Bertahan Hidup Cerdas & Penghindaran Bahaya

Untuk mencegah kematian tragis serigala jinak yang melompat ke lava atau tebing curam, Better Dogs merombak total sistem navigasinya:

1. **AI Bertahan Hidup dari Api (Lari ke Air Terdekat & Pelarian Darurat)**:
   * Ketika serigala terbakar atau menyentuh lava, sistem langsung menjalankan pencarian banjir 3D 16-blok untuk menemukan blok air terdekat yang aman.
   * Jika air ditemukan, serigala berlari kencang dengan kecepatan $1.35\times$ untuk memadamkan dirinya di dalam air.
   * Jika tidak ada air dalam radius 16 blok, serigala beralih ke mode panik darurat menjauhi sumber api untuk memperbesar peluang selamat.
2. **Pencegahan Terjun ke Jurang (*Cliff Safety Gating*)**:
   * Memeriksa penurunan elevasi lebih dari 3 blok. Jika dorongan atau langkah akan menyebabkan kerusakan jatuh parah tanpa pendaratan air yang aman di bawahnya, pergerakan tersebut akan ditolak.
3. **Penghindaran Bahaya Panas & Lingkungan**:
   * Bobot penalti navigasi tinggi diterapkan pada api, lava, api unggun (*campfire*), semak beri berduri, blok magma, dan salju bubuk (*powder snow*).
4. **Pelarian dari Ledakan Creeper**:
   * Saat Creeper di dekatnya mulai mendesis (*fuse*), serigala seketika membatalkan serangannya dan berlari ke arah berlawanan.
5. **Perlindungan Tebasan Pedang Pemilik (*Friendly Fire Sweep Protection*)**:
   * Serigala jinak kebal terhadap kerusakan tebasan area (*sweeping edge*) dari pedang pemiliknya sendiri.
6. **Makan Makanan Jatuh di Tanah (*Scavenge Feeding*)**:
   * Serigala jinak yang terluka akan mencari dan memakan daging yang tergeletak di tanah (`#vanilla-outsider-better-dogs:raw_food`, `#vanilla-outsider-better-dogs:cooked_food`) untuk memulihkan darahnya secara otomatis.

---

## 🏷️ Tag Datapack Berbasis Data (*Data-Driven Tags*)

Seluruh pemeriksaan blok lingkungan dan makanan menggunakan tag datapack dengan penanganan cadangan (*fallback*) yang aman:

* `#vanilla-outsider-better-dogs:curiosity_blocks`: Dedaunan, bunga, rumput, dan tanaman pertanian yang diselidiki oleh anak anjing.
* `#vanilla-outsider-better-dogs:treats`: Camilan bernilai tinggi (daging domba matang, sup kelinci, apel emas, kentang panggang, dll.) untuk bonus makanan favorit.
* `#vanilla-outsider-better-dogs:seats`: Tangga, lempeng (*slab*), tempat tidur, karpet wol, dan kursi tempat anjing beristirahat.
* `#c:chairs`: Tag konvensi standar komunitas untuk mendukung mod kursi dan furnitur pihak ketiga.

---

## ⚡ Rekayasa Performa Tanpa Alokasi (*Zero-Allocation*)

Memelihara kawanan serigala dalam jumlah besar di vanilla Minecraft dapat menyebabkan lonjakan lag karena pemeriksaan jalur kuadratik ($O(N^2)$). Better Dogs mengatasinya melalui optimasi alokasi memori nol:

### 1. FastRandom & Memori Bebas Alokasi
* Mengganti instansiasi `new Random()` di jalur AI utama dengan `FastRandom.INSTANCE` dan `RandomSource` bawaan entitas.
* Menghilangkan alokasi array sementara pada logika perkawinan dan pola pencarian AI.

### 2. Penjadwal AI Berbasis Peristiwa (*Event-Driven AI Scheduler*)
* Menghilangkan polling per tick dan menggantinya dengan penanganan berbasis peristiwa (misal: serangan pemain, suara mob).
* Pemeriksaan berulang dibatasi dengan interval waktu (misal: cek lolongan hanya berjalan tiap 5 detik sekali).

### 3. Caching Jalur Bersama Boids $O(N)$
* Pengikut berbagi hasil perhitungan jalur navigasi yang dihitung oleh pemimpin kawanan, menurunkan beban komputasi CPU dari kuadratik $O(N^2)$ menjadi linier $O(N)$.
