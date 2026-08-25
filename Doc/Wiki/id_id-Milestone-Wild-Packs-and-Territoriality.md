# Pembaruan: Kawanan Liar & Wilayah Kekuasaan

Diperkenalkan pada **`v4.3.0` (MC 26.2)** dan diterapkan di seluruh versi yang didukung, pembaruan ini merombak serigala liar menjadi kelompok sosial yang terorganisir, memiliki pemimpin, dan berinteraksi secara teritorial dengan kawanan lain.

---

## 🐺 Pemimpin Kawanan Permanen & Titik Jangkar

Serigala liar tidak lagi berkeliaran sendirian tanpa arah. Mereka membentuk kawanan hingga 8 anggota yang terikat pada seorang pemimpin dominan:
* **Jangkar Pemimpin**: Anggota kawanan melacak koordinat pemimpin mereka dan selalu bergerak dalam radius perimeter yang rapi.
* **Kepribadian Liar**: Perilaku seluruh kawanan dipengaruhi oleh kepribadian pemimpinnya (misal: Pemimpin Agresif memicu perburuan massal, Pemimpin Pasifis memimpin mundur saat terancam).
* **Bala Bantuan**: Ketika anggota kawanan diserang, serigala lain dalam kelompok akan bergegas datang membantu.

---

## 📏 Penskalaan Jarak Sebar Anggota Kawanan

Untuk mencegah serigala saling bertubrukan dan menumpuk saat berlari, jarak pemisahan antar anggota dihitung secara dinamis berdasarkan jumlah pengikut $N$:

\[f(N) = \text{multiplier} \times \sqrt{N - 1}\]

* **Pengali Sebaran**: Meningkatkan radius jarak seiring bertambahnya anggota kawanan agar formasi terlihat alami dan realistis.
* **GameRules**: Dapat disesuaikan melalui `bd_tamed_pack_spread_multiplier` dan `bd_wild_pack_spread_multiplier`.

---

## ⚔️ Taktik Tempur Pengepungan (*Flanking AI*)

Berbeda dengan serigala vanilla yang hanya berlari lurus dalam satu barisan, Better Dogs menerapkan AI taktik perburuan cerdas:
* **Pengepungan Dinamis**: Pemimpin kawanan menyerang target dari depan, sementara anggota lainnya menyebar ke samping dan belakang untuk menyerang dari sudut tak terduga.
* **Mengurangi Risiko Area**: Dengan formasi mengepung, kawanan tidak mudah terkena serangan tebasan pedang (*sweeping edge*) atau lemparan ramuan sekaligus.
* **Pengaturan**: Dapat diaktifkan/dinonaktifkan melalui GameRule `bd_pack_flanking_tactics`.

---

## ⚔️ Matriks Probabilitas Sengketa Wilayah

Ketika dua kawanan serigala liar bertemu di perbatasan wilayah, sistem menjalankan matriks probabilitas berdasarkan kepribadian kedua pemimpin:

| Pemimpin A | Pemimpin B | Perang (Duel/Tempur) | Penggabungan (Dominasi) | Mundur (Mengalah) |
| :--- | :--- | :--- | :--- | :--- |
| **Agresif** | **Agresif** | **$80\%$** | $10\%$ | $10\%$ |
| **Agresif** | **Normal** | **$50\%$** | $30\%$ | $20\%$ |
| **Agresif** | **Pasifis** | $10\%$ | **$60\%$** | $30\%$ |
| **Normal** | **Normal** | $30\%$ | $30\%$ | **$40\%$** |
| **Normal** | **Pasifis** | $10\%$ | $40\%$ | **$50\%$** |
| **Pasifis** | **Pasifis** | $0\%$ | $20\%$ | **$80\%$** |

---

## ⚔️ Duel 1v1 Antar Pemimpin & Penggabungan Kawanan

Jika hasil matriks menghasilkan **Perang**, perselisihan dapat diselesaikan melalui **Duel Sinematik 1v1**:
* **Arena Duel**: Kedua pemimpin berdiri di tengah arena dan bertarung satu lawan satu, sementara seluruh anggota kawanan berdiri melingkar menonton pertempuran.
* **Menyerah**: Ketika darah salah satu pemimpin turun di bawah $20\%$, ia akan menyerah (*yield*) kepada pemenang, mengakhiri duel tanpa harus mati.
* **Penggabungan Kawanan**: Pemimpin yang kalah beserta seluruh anggotanya akan **bergabung** ke dalam kawanan pemenang. Hal ini memungkinkan kawanan liar di dunia untuk secara alami berkembang menjadi koloni serigala yang besar.
