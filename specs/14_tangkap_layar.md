# Spesifikasi 14: Tangkap Layar

## Tujuan
Pengguna menangkap layar dengan perintah suara tanpa menekan tombol apa pun.

## Perilaku
1. Ucapan tangkap layar atau screenshot memicu tangkapan layar sistem.
2. Fitur memakai layanan aksesibilitas Nazeio yang dinyalakan satu kali di pengaturan aksesibilitas.
3. Bila layanan belum aktif, Nazeio membuka pengaturan aksesibilitas dan memberi tahu cara menyalakannya.
4. Hasil tangkapan disimpan sistem ke galeri seperti tangkapan layar biasa.

## Batasan
1. Layanan aksesibilitas tidak bisa dinyalakan lewat suara, kebijakan Android melarangnya.
2. Di Android 11, aksi tangkap layar aksesibilitas belum tersedia sehingga perintah dijawab dengan pesan bahwa perangkat tidak mendukung.
3. Layanan tidak membaca isi layar, hanya memicu aksi tangkap layar.

## Di luar cakupan
1. Pengeditan atau pengiriman hasil tangkapan.
2. Rekaman layar.

## Kriteria selesai
1. Setelah layanan dinyalakan, tangkap layar menyimpan tangkapan di galeri.
2. Sebelum layanan dinyalakan, perintah mengarahkan pengguna ke pengaturan aksesibilitas.
3. Perangkat yang tidak mendukung dijawab dengan pesan gagal, bukan diam.
