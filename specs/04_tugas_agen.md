# Spesifikasi 04: Tugas Berantai oleh Agen

## Tujuan

Pengguna memberi tugas besar dengan suara, misalnya "buatkan website, push ke GitHub, atur Supabase, lalu deploy ke Vercel", dan Nazeio mengerjakannya lewat agen di cloud.

## Perilaku

1. Nazeio mengenali ucapan sebagai tugas besar.
2. Nazeio menyusun rencana singkat dan membacakannya.
3. Pengguna mengonfirmasi dengan suara atau tombol sebelum langkah pertama dijalankan.
4. Agen menjalankan langkah satu per satu: membuat proyek, commit dan push ke GitHub, mengatur Supabase, deploy ke Vercel.
5. Nazeio melaporkan progres setiap langkah, di layar dan dengan suara singkat.
6. Pada langkah berisiko, Nazeio berhenti dan meminta konfirmasi baru.
7. Setelah selesai, Nazeio menyebutkan hasil dan menampilkan tautan.
8. Jika ada langkah gagal, Nazeio menjelaskan penyebabnya dan menawarkan ulang atau berhenti.
9. Pengguna dapat membatalkan kapan saja.

## Langkah Berisiko

Wajib konfirmasi baru dan ditandai merah:
1. Push ke repositori.
2. Deploy ke produksi.
3. Mengubah atau menghapus data di database.
4. Menghapus repositori atau proyek.

## Keamanan

1. Token GitHub, Supabase, dan Vercel disimpan di Android Keystore dengan izin sekecil mungkin.
2. Ucapan bisa salah dengar, jadi rencana selalu dibacakan ulang sebelum dijalankan.
3. Semua aksi dicatat dalam log yang dapat dilihat pengguna.
4. Ada batas biaya per tugas dan per hari.
5. Agen tidak boleh menjalankan aksi di luar daftar alat yang disetujui.

## Batasan

1. Membutuhkan internet dan API model bahasa berbayar.
2. Agen dapat memakai Naze Coding Agent atau Naze Orchestra sebagai pelaksana.
3. Waktu pengerjaan bisa lama, sehingga tugas berjalan di Foreground Service dengan notifikasi.

## Di luar cakupan

1. Menjalankan aksi produksi tanpa konfirmasi.
2. Tugas yang melibatkan pembayaran.
3. Mengakses akun selain yang diizinkan pengguna.

## Kriteria selesai

1. Satu tugas contoh selesai dari ucapan sampai tautan hasil tanpa campur tangan lain selain konfirmasi.
2. Setiap langkah berisiko benar benar berhenti menunggu konfirmasi.
3. Membatalkan di tengah jalan menghentikan langkah berikutnya.
4. Token tidak pernah muncul di log atau layar.
5. Batas biaya menghentikan tugas dan memberi tahu pengguna.
