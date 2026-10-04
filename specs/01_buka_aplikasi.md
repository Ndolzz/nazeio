# Spesifikasi 01: Buka Aplikasi dengan Suara

## Tujuan

Pengguna mengucapkan nama aplikasi apa pun yang terpasang, lalu Nazeio membukanya.

## Perilaku

1. Pengguna menekan tombol mikrofon atau memicu asisten.
2. Nazeio menampilkan indikator mendengar berwarna ungu.
3. Ucapan diubah menjadi teks.
4. Teks dinormalisasi: huruf kecil, tanda baca dibuang, kata pengantar seperti "tolong" dan "nazeio" dibuang.
5. Nama aplikasi dicocokkan dengan nama tampilan aplikasi terpasang dan dengan alias dari spesifikasi 02.
6. Jika tepat satu yang cocok, aplikasi dibuka dan Nazeio menjawab dengan suara "Membuka" diikuti nama aplikasi.
7. Jika beberapa yang cocok, Nazeio menyebut pilihan dan bertanya mana yang dimaksud.
8. Jika tidak ada yang cocok, Nazeio menjawab "Aplikasi tidak ditemukan".
9. Jika tidak ada ucapan terdengar, Nazeio menjawab "Tidak terdengar, coba lagi".

Contoh ucapan: "buka youtube", "buka aplikasi telegram", "tolong buka kamera".

## Batasan

1. Izin yang dibutuhkan: RECORD_AUDIO dan QUERY_ALL_PACKAGES.
2. Daftar aplikasi dibaca dari PackageManager, termasuk nama tampilannya.
3. Toleransi salah ucap memakai ambang kemiripan yang dapat diatur, nilai awal 0,75.
4. Jika izin ditolak, Nazeio menjelaskan izin yang dibutuhkan dan tidak berhenti mendadak.
5. Kandidat pengenal suara: SpeechRecognizer bawaan Android, sherpa onnx, atau whisper. Keputusan diambil setelah uji di HP target.

## Di luar cakupan

1. Mendengar terus menerus.
2. Kata bangun seperti "hei Nazeio".
3. Perintah selain membuka aplikasi.

## Kriteria selesai

1. Mengucapkan "buka youtube" membuka YouTube dalam 3 detik pada HP uji.
2. Mengucapkan "buka sportify" tetap membuka Spotify.
3. Aplikasi yang tidak terpasang menghasilkan jawaban "Aplikasi tidak ditemukan".
4. Dua aplikasi dengan nama mirip menghasilkan pertanyaan balik.
5. Menolak izin mikrofon tidak membuat aplikasi tertutup.
6. Pengurai teks lolos tes otomatis untuk minimal 20 contoh ucapan.
