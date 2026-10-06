# Nazeio

Nazeio adalah asisten suara pribadi untuk Android. Aplikasi mendengarkan kata pemicu Nazeio, lalu menjalankan perintah suara dalam bahasa Indonesia dan menjawab dengan suara.

## Fitur

1. Mode siaga dengan kata pemicu Nazeio. Percakapan berlanjut sampai pengguna diam 8 detik, berkata selesai, atau menekan tombol berhenti.
2. Buka aplikasi dengan pencocokan nama tahan salah ucap. Kecocokan di bawah 0,9 dimintai konfirmasi lisan.
3. Buka panel pengaturan lewat alias suara yang tersimpan sebagai data.
4. Buka WhatsApp, cari di YouTube, dan cari di Google.
5. Tanya jawab dengan Gemini atau Claude memakai kunci API milik pengguna. Kunci disimpan terenkripsi dan ada batas pemakaian harian.
6. Tiga widget berukuran 2 x 2, 4 x 1, dan 4 x 2 dengan status Mati, Siaga, dan Aktif.
7. Pengingat suara, misalnya ingatkan aku lima menit lagi, atau pukul 7. Alarm dipasang presisi bila izin alarm presisi diberikan. Pengingat bertahan setelah HP dinyalakan ulang, bisa dilihat dan dihapus dari layar Pengingat.
8. Telepon lewat dialer dengan nama kontak atau nomor. Kecocokan kontak di bawah 0,9 dimintai konfirmasi lisan. Aplikasi tidak pernah menelepon langsung.
9. Pertanyaan lokal tanpa internet: jam, hari, tanggal, baterai, dan hitungan hari menuju tanggal tertentu.
10. Riwayat perintah tersimpan permanen, dimuat kembali saat aplikasi dibuka, dan bisa dikosongkan dengan tombol hapus semua.
11. Kontrol Bluetooth lewat suara: nyalakan bluetooth, matikan bluetooth, dan tanya status.
12. Tangkap layar lewat perintah suara setelah layanan aksesibilitas Nazeio dinyalakan satu kali di pengaturan.
13. Maps lewat suara: buka maps, cari X di maps, dan arah ke X membuka navigasi.

## Batasan

1. Android 11 ke atas.
2. Target utama HP ARMv7 32 bit.
3. Bahasa utama Indonesia.

## Pengenal Suara

Nazeio memakai pengenal suara bawaan HP bila tersedia: mode offline lebih dulu, lalu otomatis beralih ke online bila model offline tidak ada.

Catatan: belum ada model offline Bahasa Indonesia yang layak untuk Vosk resmi (model Bookbot dilatih dari suara anak-anak). Rencana pengenal on-device sepenuhnya offline sesuai spesifikasi 05 menyusul lewat sherpa-onnx (keyword spotting untuk pemicu + Whisper kecil untuk percakapan).

## Cara Build

Butuh JDK 17.

1. Jalankan gradle wrapper bila belum ada, lalu ./gradlew assembleDebug.
2. APK debug ada di app/build/outputs/apk/debug.
3. CI GitHub Actions membangun APK dan menjalankan tes unit pada setiap push ke main, hasilnya tersimpan sebagai artifact.

## Struktur Repositori

1. specs berisi spesifikasi tiap fitur, satu berkas untuk satu fitur.
2. desain berisi rancangan tampilan dalam HTML.
3. CATATAN_WIDGET.md berisi catatan rilis versi 0.5.0.

## Spesifikasi

1. 01_buka_aplikasi.md
2. 02_alias.md
3. 03_tanya_jawab.md
4. 04_tugas_agen.md
5. 05_pemicu_suara.md
6. 06_widget_dan_animasi.md
7. 07_pengingat.md
8. 08_telepon.md
9. 09_waktu_tanggal.md
10. 10_riwayat_permanen.md
11. 11_pencarian_google.md
12. 12_layar_pengingat.md
13. 13_bluetooth.md
14. 14_tangkap_layar.md
15. 15_maps.md

## Lisensi

Kode dalam repositori ini dilisensikan di bawah MIT. Lihat berkas LICENSE.
