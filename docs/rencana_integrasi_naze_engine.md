# Rencana Integrasi Teknis Naze Engine

Status: Draf. Referensi spesifikasi 16. Pekerjaan dimulai setelah M007 pada repositori `model-naze2.0` selesai dan pemilik proyek menyetujui batas ukuran model (OD-118).

## 1. Ringkasan

Integrasi mencakup tiga lapisan pekerjaan pada dua repositori.

1. Ekspor bobot pada repositori `model-naze2.0`: skrip menyimpan parameter model terlatih ke berkas npz.
2. Pembaca berkas dan inferensi pada repositori nazeio: modul Kotlin membaca npz lalu menjalankan model tanpa framework deep learning.
3. Penyambungan alur aplikasi pada nazeio: teks hasil pengenal suara masuk ke Naze Engine sebelum rute tanya jawab spesifikasi 03.

## 2. Alur data

1. Teks hasil pengenal suara dinormalisasi: huruf kecil, spasi dirapikan, tanda baca dibuang.
2. Teks disusun ke templat korpus, contohnya "perintah: {teks}" diikuti penanda awal jawaban.
3. Naze Engine menjalankan inferensi greedy memakai bobot npz dan menghasilkan keluaran teks pendek.
4. Keluaran dicocokkan secara deterministik ke daftar intent Tingkat 1 memakai alias dari spesifikasi 02.
5. Bila cocok, aksi lokal dijalankan tanpa internet. Bila tidak cocok atau skor rendah, perintah diteruskan ke tanya jawab spesifikasi 03.

## 3. Ekspor bobot pada model-naze2.0

1. Skrip baru `src/naze/export.py` mengambil kamus `params()` dari `TransformerLM` lalu menyimpannya ke berkas npz.
2. Nama kunci sama dengan kamus datar model: `emb.E`, `pos.P`, `blk{i}.attn.{q,k,v,o}.{W,b}`, `blk{i}.attn.norm.{gamma,beta}`, `blk{i}.ffn.fc{1,2}.{W,b}`, `blk{i}.ffn.norm.{gamma,beta}`, `final.{gamma,beta}`, `head.{W,b}`.
3. Bobot dikonversi dari float64 ke float32 supaya ukuran berkas kecil. Urutan kunci tetap dan stempel waktu berkas zip tetap supaya isi berkas identik antar pembuatan ulang.
4. Berkas memuat satu kunci metadata berisi: d_model, num_heads, num_layers, d_ff, max_sequence_length, vocab_size 256, versi bobot, dan checksum SHA 256.
5. Estimasi ukuran: jumlah parameter untuk d_model 64, 2 lapis, d_ff 128, max_sequence_length 64 sekitar 103 ribu sehingga berkas float32 sekitar 0,4 MB. Jauh di bawah batas 20 MB.
6. Setiap versi bobot dan hasil evaluasinya dicatat pada folder docs di repositori `model-naze2.0` sesuai kriteria selesai spesifikasi 16.

## 4. Pembaca npz pada Kotlin

1. npz adalah arsip zip berisi berkas npy. Kotlin memakai `java.util.zip.ZipFile` tanpa pustaka luar.
2. Pembaca npy minimal memeriksa magic `0x93NUMPY`, membaca versi, panjang header, kamus header, lalu data float32 little endian.
3. Setiap tensor disimpan sebagai FloatArray datar beserta dimensi bentuknya. Tidak ada objek tensor umum supaya jejak memori kecil.

## 5. Inferensi Kotlin

1. Modul naze di dalam aplikasi berisi: pemuat bobot, util matmul dan softmax bergeser maksimum, tanh, LayerNorm dengan eps 1e-5, mask kausal memakai baris maksimum dikurangi 1e9, maju transformer, dan dekode greedy dengan konteks dipotong ke max_sequence_length.
2. Semua hitungan float32 dengan urutan operasi tetap dan satu thread supaya hasil deterministik sesuai kriteria spesifikasi 16.
3. API publik: `NazeEngine.init(assets)` memuat bobot sekali saat aplikasi menyala, `NazeEngine.proses(teks)` mengembalikan intent dan skor keyakinan.
4. Bila berkas bobot hilang atau rusak, Naze Engine melaporkan tidak siap dan semua perintah diteruskan ke spesifikasi 03. Rute lama tetap berfungsi.

## 6. Korpus perintah dan evaluasi

1. Korpus pelatihan perintah Tingkat 1 dibangun di repositori `model-naze2.0` dengan templat "perintah: {teks}" lalu jawaban intent. Basis D-016 diperluas dengan variasi ejaan percakapan sehari hari.
2. Daftar perintah uji memuat buka aplikasi, buka pengaturan, pasang pengingat, bluetooth, tangkap layar, dan variasi aliasnya. Akurasi diukur memakai daftar ini.
3. Latihan berjalan di komputer, bukan di perangkat. Hanya berkas bobot hasil latihan yang dibawa ke nazeio.

## 7. Uji kesetaraan dan determinisme

1. Skrip ekspor juga mengeluarkan masukan tetap dan logits referensi dari Python sebagai berkas JSON.
2. Unit test Kotlin membandingkan hasil inferensi dengan referensi dalam toleransi 1e-4 karena Python float64 dan Kotlin float32.
3. Test determinisme memanggil proses dua kali dengan masukan sama dan memastikan hasil identik bit per bit.
4. Test kegagalan memastikan berkas bobot rusak membuat Naze Engine melaporkan tidak siap tanpa membuat aplikasi berhenti.

## 8. Tahapan pekerjaan

1. Selesaikan tugas M007 T015 sampai T018 pada repositori `model-naze2.0`.
2. Pemilik proyek menyetujui batas ukuran model pada OD-118. Usulan batas 5 MB, jauh di bawah 20 MB.
3. Tugas ekspor bobot, korpus perintah, dan evaluasi pada repositori `model-naze2.0`.
4. Modul Kotlin pembaca npy dan unit test golden pada nazeio.
5. Penyambungan alur sebelum rute spesifikasi 03 beserta test offline.
6. Uji pada HP target ARMv7: waktu jawab di bawah 1 detik dan tambahan memori di bawah 20 MB.
7. Naikkan status spesifikasi 16 ke Disetujui bila semua kriteria selesai terpenuhi.

## 9. Risiko

1. Drift hasil antara float64 dan float32 dijaga dengan test golden dan toleransi.
2. ARMv7 32 bit tanpa SIMD membuat hitungan lambat. Model sengaja kecil dan waktu diukur dini pada tahap 6.
3. Berkas npz bisa berubah byte antar pembuatan karena stempel waktu zip. Skrip ekspor memakai stempel tetap.
4. Model bahasa kecil bisa gagal pada perintah di luar korpus. Rute fallback ke spesifikasi 03 selalu tersedia.
5. Ukuran APK bertambah karena bobot di assets. CI memeriksa ukuran APK pada setiap push.

## 10. Di luar cakupan

1. Pengenalan suara dan kata pemicu tetap pada spesifikasi 05.
2. Pelatihan atau penyempurnaan model di perangkat.
3. Menggantikan API Gemini atau Claude untuk tanya jawab berat.
