# Spesifikasi 16: Naze Engine di Perangkat

## Tujuan

Memakai model bahasa Naze dari repositori `model-naze2.0` sebagai komponen bahasa di dalam perangkat. Naze Engine menerima teks hasil pengenal suara, mengenali perintah Tingkat 1, dan menjalankan aksi lokal tanpa internet.

## Perilaku

1. Teks hasil pengenal suara masuk lebih dulu ke Naze Engine sebelum memakai API model bahasa luar.
2. Naze Engine mengenali perintah ringan, misalnya buka aplikasi, buka pengaturan, dan pasang pengingat, lalu menjalankan aksi lokal.
3. Bila Naze Engine tidak yakin, perintah diteruskan ke tanya jawab pada spesifikasi 03.
4. Naze Engine berjalan tanpa internet sehingga perintah ringan tetap berfungsi saat offline.
5. Model dimuat dari berkas bobot saat aplikasi menyala. Tidak ada pelatihan di perangkat.
6. Hasil Naze Engine deterministik. Uji ulang dengan masukan sama memberi hasil sama.

## Batasan

1. Perangkat target ARMv7 32 bit sehingga model harus kecil. Batas ukuran model ditetapkan pemilik proyek pada repositori `model-naze2.0` sebelum integrasi dimulai.
2. Model Naze masih dalam pengembangan pada milestone M007. Integrasi dimulai setelah M007 selesai dan hasil evaluasinya diterima.
3. Bobot memakai format npz dari repositori `model-naze2.0`. Inferensi berjalan di Kotlin tanpa framework deep learning.
4. Naze Engine menerima teks, bukan audio. Pengenal suara tetap mengikuti spesifikasi 05.
5. Kosakata model byte level 256, sesuai desain model Naze.

## Di luar cakupan

1. Pengenalan suara dan kata pemicu.
2. Pelatihan atau penyempurnaan model di perangkat.
3. Menggantikan API Gemini atau Claude untuk tanya jawab berat.

## Kriteria selesai

1. Perintah Tingkat 1 dikenali Naze Engine tanpa internet. Akurasi diukur memakai daftar perintah uji.
2. Waktu jawab Naze Engine di bawah 1 detik pada HP target.
3. Tambahan memori aplikasi di bawah 20 MB saat model dimuat.
4. Versi bobot dan hasil uji tercatat pada repositori `model-naze2.0`.
5. Saat Naze Engine gagal mengenali, jalur tanya jawab spesifikasi 03 tetap berfungsi.

## Status

Draf. Menunggu persetujuan pemilik proyek dan hasil milestone M007.
