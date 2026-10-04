# Nazeio: Spesifikasi Induk

Nazeio adalah asisten Android pribadi yang dikendalikan dengan suara. Dokumen ini menjadi acuan bagi semua spesifikasi lain.

## Tujuan

Memungkinkan pengguna menjalankan tugas di HP dan di layanan pengembangan hanya dengan berbicara, dengan hasil yang cepat, aman, dan mudah dipahami.

## Tingkat Kemampuan

1. Tingkat 1: perintah ringan di perangkat, yaitu membuka aplikasi, membuka panel pengaturan, membuka chat WhatsApp, dan mengambil screenshot. Berjalan tanpa internet bila memungkinkan. Lihat spesifikasi 01 dan 02.
2. Tingkat 2: tanya jawab dan bantuan koding melalui API model bahasa. Lihat spesifikasi 03.
3. Tingkat 3: tugas berantai oleh agen, misalnya membuat website, push ke GitHub, mengatur Supabase, dan deploy ke Vercel. Lihat spesifikasi 04.

## Prinsip

1. Perintah ringan harus selesai tanpa bergantung pada layanan luar.
2. Alias disimpan sebagai data, bukan di dalam kode.
3. Aksi yang tidak bisa dibatalkan selalu meminta konfirmasi lisan.
4. Token dan kunci disimpan di Android Keystore.
5. Urutan kerja: tulis spesifikasi, implementasikan, uji terhadap kriteria selesai, perbarui spesifikasi bila perilaku berubah.
6. Satu spesifikasi untuk satu fitur.

## Batasan Platform

1. Android 11 sebagai versi minimal.
2. Arsitektur ARMv7 (32 bit), sehingga model lokal harus kecil.
3. Bahasa utama adalah Indonesia.

## Tumpukan Teknologi

Kotlin, Jetpack Compose, Room, Foreground Service, AccessibilityService, VoiceInteractionService, dan Android Keystore. Suara bicara memakai Piper dengan suara id_ID news_tts medium. Pengenal suara ditentukan di spesifikasi 01 setelah uji coba.

## Pedoman Tampilan

Gaya minimalis, rapi, bersih, dan profesional.

Warna:
1. Putih, #FFFFFF, latar utama.
2. Putih abu, #F8FAFC, kartu dan lapisan.
3. Biru, #2563EB, warna utama untuk tombol dan tautan.
4. Ungu, #7C3AED, aksen untuk asisten dan proses AI.
5. Merah, #DC2626, hanya untuk peringatan dan aksi yang tidak bisa dibatalkan.
6. Teks, #0F172A, teks utama.
7. Teks redup, #64748B, keterangan.

Aturan:
1. Biru dominan, ungu hanya untuk hal yang berkaitan dengan asisten, merah dipakai sangat hemat.
2. Satu keluarga huruf sans serif, empat tingkat ukuran.
3. Jarak kelipatan 8, sudut membulat 12, bayangan tipis atau tanpa bayangan.
4. Ikon garis sederhana.
5. Mode gelap memakai latar hitam kebiruan dengan warna aksen yang sama.

## Aturan Penulisan

1. Tanpa emoji di aplikasi maupun dokumen.
2. Tanpa tanda strip di teks aplikasi maupun dokumen. Gunakan titik, koma, atau baris baru.
3. Kalimat singkat dan langsung.

## Daftar Spesifikasi

1. 01_buka_aplikasi.md
2. 02_alias.md
3. 03_tanya_jawab.md
4. 04_tugas_agen.md

## Kerangka Setiap Spesifikasi

Tujuan, Perilaku, Batasan, Di luar cakupan, Kriteria selesai.

## Risiko Utama

1. Pengenalan suara bahasa Indonesia secara offline di ARMv7 belum terbukti.
2. Mendengar terus di latar belakang menguras baterai.
3. Android membatasi AccessibilityService dan akses mikrofon di latar belakang.
4. Ucapan bisa salah dengar, sehingga aksi berisiko wajib dikonfirmasi.
