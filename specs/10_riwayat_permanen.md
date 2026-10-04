# Spesifikasi 10: Riwayat Permanen

## Tujuan
Riwayat perintah yang pernah dijalankan Nazeio bertahan setelah aplikasi ditutup atau HP dimatikan ulang, dan tampil kembali di bagian Terakhir pada Beranda serta layar Riwayat.

## Perilaku
1. Setiap perintah yang diproses dicatat bersama jawabannya dan waktu pelaksanaan.
2. Riwayat dimuat dari database saat aplikasi dibuka.
3. Database menyimpan paling banyak 50 baris terakhir. Baris lama dipangkas otomatis.
4. Riwayat terbaru selalu tampil paling atas.
5. Tombol hapus semua di layar Riwayat mengosongkan riwayat di layar dan di database sekaligus.

## Batasan
1. Riwayat tidak tersinkron antar perangkat.
2. Hapus hanya berlaku untuk seluruh riwayat, belum per baris.

## Di luar cakupan
1. Pencarian di dalam riwayat.
2. Ekspor riwayat.

## Kriteria selesai
1. Perintah yang dijalankan tetap tercatat setelah aplikasi ditutup lalu dibuka kembali.
2. Jumlah baris yang tersimpan tidak pernah lebih dari 50.
3. Setelah tombol hapus semua ditekan, layar dan database benar benar kosong.
