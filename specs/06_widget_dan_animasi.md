# Spesifikasi 06: Widget dan Animasi

## Tujuan

Widget Nazeio tampil rapi di layar utama, menunjukkan status dengan jelas, dan memiliki animasi serta neon yang gayanya dapat dipilih pengguna di Pengaturan.

## Ukuran

1. Dua kali dua: mikrofon di tengah, tombol siaga di sudut.
2. Empat kali satu: satu baris, mikrofon, nama, status, dan tombol siaga.
3. Empat kali dua: baris atas seperti empat kali satu, bagian bawah mengikuti status.

## Status

1. Mati: abu abu, tanpa neon, ikon mikrofon biasa. Bagian bawah widget besar menampilkan tombol Nyalakan.
2. Siaga: biru, neon berdenyut pelan, animasi lambat. Bagian bawah widget besar menampilkan pintasan cepat.
3. Aktif: ungu, neon berputar, animasi cepat. Bagian bawah widget besar menampilkan teks ucapan dan gelombang suara.
4. Galat mikrofon: merah, dengan pesan singkat dan tombol coba lagi.

## Gaya Animasi

Pengguna memilih tepat satu gaya di Pengaturan, pada bagian Tampilan:
1. Gelombang: lima batang yang naik turun seperti equalizer. Gaya bawaan.
2. Kipas: tiga bilah yang berputar.
3. Badai: tiga lingkaran bergaris yang berputar berlawanan arah.

## Perilaku

1. Pengaturan memiliki pratinjau langsung untuk tiga gaya dalam status Siaga dan Aktif.
2. Gaya yang dipilih berlaku untuk semua widget dan untuk gelembung melayang.
3. Pilihan disimpan di DataStore dan bertahan setelah aplikasi ditutup.
4. Mengubah gaya memperbarui semua widget yang sudah terpasang dalam 2 detik.
5. Kecepatan animasi mengikuti status: lambat saat Siaga, cepat saat Aktif.
6. Ketuk mikrofon memulai atau mengakhiri percakapan. Geser tombol menyalakan atau mematikan mode siaga. Ketuk nama Nazeio membuka aplikasi.
7. Opsi Hemat baterai di Pengaturan mengurangi jumlah bingkai animasi dan mematikan neon berputar.
8. Jika HP mengaktifkan pengurangan gerak, animasi diganti ikon statis berwarna.

## Batasan

1. Widget Android asli tidak menjalankan animasi sendiri. Animasi dibuat dengan mengganti gambar bingkai, sekitar 8 sampai 12 bingkai per detik, dari layanan siaga yang sudah berjalan.
2. Saat Siaga cukup 2 sampai 4 bingkai per detik agar hemat baterai.
3. Sistem atau launcher dapat membatasi pembaruan yang terlalu sering. Kehalusan harus diuji di HP target.
4. Animasi penuh dan halus hanya tersedia di gelembung melayang yang memakai Compose, aktif hanya saat status Aktif.
5. Teknologi widget: Jetpack Glance atau App Widget klasik. Keputusan diambil setelah uji performa.

## Di luar cakupan

1. Gaya animasi buatan pengguna.
2. Memilih gaya berbeda untuk setiap widget.
3. Mengganti warna neon.

## Kriteria selesai

1. Ketiga gaya dapat dipilih dan pilihannya bertahan setelah HP dihidupkan ulang.
2. Mengubah gaya memperbarui widget yang terpasang dalam 2 detik.
3. Neon tampil di status Siaga dan Aktif, dan hilang di status Mati.
4. Mode Hemat baterai menurunkan pemakaian baterai dibanding mode biasa pada uji 1 jam status Aktif.
5. Tombol geser di widget menyalakan dan mematikan mode siaga tanpa membuka aplikasi.
6. Tidak ada emoji dan tidak ada tanda strip pada teks widget maupun Pengaturan.
