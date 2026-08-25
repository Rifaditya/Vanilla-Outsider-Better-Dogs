# Pembaruan: Mode Penjaga & Anjing Sentinel

Diperkenalkan pada **`v4.8.0` (MC 26.2)** dan disempurnakan di seluruh versi modern, pembaruan ini mengimplementasikan sistem anjing penjaga peliharaan, sikap siaga waspada (*watchdog alarm*), semprotan partikel trigonometri berarah, pelindung interaksi 6-dimensi, dan jeda lolongan bulan kelompok.

---

## 🛡️ Mode Penjaga Peliharaan (*Domestic Guard Mode*)

Serigala jinak dapat ditugaskan untuk menjaga pos tertentu atau berpatroli:
* **Aktivasi**: Dibatasi hanya untuk pemilik sah. Jongkok (Shift) + Klik Kanan serigala sambil memegang tulang untuk mengaktifkan/menonaktifkan **Mode Penjaga** (mengonsumsi 1 tulang dalam mode bertahan hidup).
* **Penjaga Interaksi 6-Dimensi**: Menghilangkan pemicuan ganda tangan kiri/kanan (*debounce*), mempertahankan postur duduk/berdiri tanpa perubahan tidak disengaja, mendukung bypass mode kreatif, dan memvalidasi izin UUID pemilik.
* **Pengecekan Garis Pandang (*Line-of-Sight*)**: Anjing penjaga membutuhkan garis pandang bebas untuk menarget musuh, mencegah mereka menyerang monster yang terhalang dinding tebal (seperti monster di dalam gua di bawah lantai rumah).

### Pola Patroli Kepribadian
Setiap kepribadian serigala melakukan pola patroli yang berbeda di sekitar pos jaganya:
* **Agresif**: Berpatroli di perimeter lingkaran luar pada $80\%$ dari jarak jangkauan maksimal (radius bawaan 12 blok), berhenti sejenak untuk memindai ancaman ke arah luar.
* **Normal**: Sikap siaga tepat di koordinat pos (jika jarak 0) atau berjalan melintasi jalur radial maju-mundur (radius bawaan 8 blok).
* **Pasifis**: Berpatroli dalam orbit lingkaran pelindung dekat pos penjaga (radius bawaan 4 blok).

---

## 🚨 Alarm Pengawas & Sikap Siaga Waspada

Ketika anjing Pasifis dalam Mode Penjaga mendeteksi monster jahat dalam radius 16 blok (dengan pendengaran vertikal hingga 4 blok menembus dinding), ia memicu **Alarm Pengawas (*Watchdog Alarm*)**:
1. **Sikap Siaga**: Serigala langsung berdiri seketika, menghentikan navigasi jalurnya, dan mengunci arah kepala serta badannya menghadap langsung ke arah ancaman terdekat setiap tick.
2. **Peringatan Suara & Visual**: Serigala merengek waspada dan menembakkan semprotan partikel penunjuk arah.
3. **Buff Pemilik**: Memberikan efek status Regenerasi dan Ketahanan (*Resistance*) kepada pemilik dan sekutu di sekitarnya jika GameRule diaktifkan.

---

## 📐 Semprotan Partikel Trigonometri Berarah

Saat Alarm Pengawas aktif, partikel debu merah ditembakkan ke arah depan sesuai dengan sudut pandang serigala.

### 1. Perhitungan Arah Partikel
Sistem memproyeksikan kerucut 3D bersudut $60^\circ$ yang berpusat pada vektor pandangan serigala:
* Mengambil vektor pandangan 3D: $\vec{L} = (L_x, L_y, L_z)$.
* Menghitung sudut yaw: $\theta = \text{atan2}(L_z, L_x)$ dan pitch: $\phi = L_y$.
* Menghitung panjang horizontal: $H = \sqrt{L_x^2 + L_z^2}$.
* Untuk setiap partikel, memutar yaw sebesar offset $\Delta\theta$ di dalam kerucut $60^\circ$:
  \[v_x = \cos(\theta + \Delta\theta) \times H\]
  \[v_z = \sin(\theta + \Delta\theta) \times H\]
* Partikel dimunculkan di depan wajah setinggi mulut ($y = \text{eyeHeight} - 0.1$).

### 2. Kerapatan Partikel yang Dapat Disesuaikan
Pengaturan sisi klien `guardParticleDensity` di menu Cloth Config / YACL mengatur jumlah partikel yang muncul:
* **Tinggi (*High*)**: Memunculkan 12 partikel (jarak $30^\circ$).
* **Sedang (*Medium*, Bawaan)**: Memunculkan 6 partikel (jarak $60^\circ$).
* **Rendah (*Low*)**: Memunculkan 3 partikel (jarak $120^\circ$).
* **Mati (*Off*)**: Menonaktifkan partikel sepenuhnya.

---

## 🌕 Jeda Lolongan Bulan Kelompok (*Lunar Howling*)

Untuk mencegah kawanan serigala yang besar melolong terus-menerus dan menimbulkan kebisingan suara:
1. **Pemeriksaan Berkala**: Pengecekan lolongan hanya berjalan sekali setiap 100 tick ($5$ detik).
2. **Jeda Bersama Kawanan**: Saat seekor serigala memulai lolongan kelompok saat bulan purnama, serigala lain dalam kawanan ikut melolong dan seluruhnya mendapatkan jeda waktu pendinginan (*cooldown*) selama 10 menit ($12.000$ tick).
